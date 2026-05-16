package com.devlink.global.portal;

import com.devlink.global.exception.CustomException;
import com.devlink.global.exception.ErrorCode;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 가천대 포털 SSO 인증 서비스
 * user.md의 Jsoup 크롤링 로직을 기반으로 구현
 * 각 단계별 로그를 출력하여 디버깅 지원
 *
 * @since 2026.05.16
 * @version 1.0.0
 * @author 신태훈, 조하겸
 */
@Service
public class PortalAuthService {

	private static final Logger log = LoggerFactory.getLogger(PortalAuthService.class);

	@Value("${portal.main}")
	private String portalMain;

	@Value("${portal.sso.base}")
	private String ssoBase;

	@Value("${portal.home}")
	private String portalHome;

	private static final String USER_AGENT =
		"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/120.0.0.0 Safari/537.36";

	/**
	 * 포털 SSO 인증 수행
	 * user.md: 쿠키 3개 반환 시 로그인 성공으로 판정
	 *
	 * @param portalId 포털 아이디
	 * @param password 포털 비밀번호
	 * @return 인증 성공 여부
	 * @throws CustomException 포털 연결 실패 또는 인증 실패 시
	 */
	public boolean authenticate(String portalId, String password) {
		try {
			Map<String, String> cookies = login(portalId, password);
			/* 
			 * user.md: 기본적으로 쿠키 3개 반환 시 성공이나,
			 * 비밀번호 만료(exPassword) 캠페인 페이지인 경우 인증 자체는 성공한 것이므로 우회 처리 
			 */
			boolean success = cookies != null && (cookies.size() >= 3 || cookies.containsKey("PASS_EXPIRED_SUCCESS"));
			log.info("[포털 인증] 최종 결과: {} / 쿠키 수: {}", success ? "성공" : "실패", cookies != null ? cookies.size() : 0);
			return success;
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			log.error("[포털 인증] 예상치 못한 오류: {}", e.getMessage(), e);
			throw new CustomException(ErrorCode.PORTAL_CONNECTION_ERROR);
		}
	}

	/**
	 * 포털 로그인 수행 및 쿠키 반환
	 * user.md의 로직을 그대로 구현
	 */
	private Map<String, String> login(String portalId, String password) throws Exception {
		Map<String, String> cookies = new HashMap<>();

		// 1단계: 포털 접근 → SSO 리다이렉트
		Connection.Response portalInitRes = Jsoup.connect(portalMain)
			.userAgent(USER_AGENT)
			.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
			.header("Accept-Language", "ko-KR,ko;q=0.9")
			.ignoreHttpErrors(true)
			.followRedirects(true)
			.timeout(10000)
			.execute();

		cookies.putAll(portalInitRes.cookies());

		Thread.sleep(500);

		String ssoLoginUrl = portalInitRes.url().toString();

		Document ssoDoc = portalInitRes.parse();
		Element loginForm = null;

		for (Element form : ssoDoc.select("form")) {
			if (!form.select("input[name=c_token]").isEmpty()) {
				loginForm = form;
				break;
			}
		}
		
		if (loginForm == null) {
			log.error("[인증오류] c_token 폼을 찾을 수 없음. ssoLoginUrl={}", ssoLoginUrl);
			return null;
		}

		String formAction = loginForm.attr("action");
		if (formAction.startsWith("/")) formAction = ssoBase + formAction;

		String lToken = loginForm.select("input[name=l_token]").attr("value");
		String cToken = loginForm.select("input[name=c_token]").attr("value");

		// 2단계: 로그인 POST
		Connection.Response loginRes = Jsoup.connect(formAction)
			.userAgent(USER_AGENT)
			.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
			.header("Accept-Language", "ko-KR,ko;q=0.9")
			.referrer(ssoLoginUrl)
			.cookies(cookies)
			.data("l_token", lToken)
			.data("c_token", cToken)
			.data("user_timezone_offset", "-540")
			.data("user_id", portalId)
			.data("user_password", password)
			.method(Connection.Method.POST)
			.ignoreHttpErrors(true)
			.followRedirects(false)
			.timeout(10000)
			.execute();

		cookies.putAll(loginRes.cookies());

		if (loginRes.statusCode() == 200 && loginRes.header("Location") == null) {
			if (loginRes.body().contains("exPassword")) {
				log.warn("[인증성공-예외] 비밀번호 변경 권고 페이지 감지. ID/PW는 일치하므로 인증 성공으로 간주합니다.");
				cookies.put("PASS_EXPIRED_SUCCESS", "true");
				return cookies; // 여기서 즉시 종료하여 성공 처리
			}
			log.warn("[인증경고] POST 리다이렉트가 아님(200 OK). 바디 일부: {}", 
				loginRes.body().length() > 500 ? loginRes.body().substring(0, 500) : loginRes.body());
		}

		// 3단계: 리다이렉트 체인 추적
		String nextUrl = loginRes.header("Location");
		Connection.Response lastRes = loginRes;
		int count = 0;

		while (nextUrl != null && !nextUrl.isEmpty() && count < 10) {
			if (nextUrl.startsWith("/")) {
				String base = lastRes.url().toString();
				String host = base.substring(0, base.indexOf("/", 8));
				nextUrl = host + nextUrl;
			}

			lastRes = Jsoup.connect(nextUrl)
				.userAgent(USER_AGENT)
				.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
				.header("Accept-Language", "ko-KR,ko;q=0.9")
				.referrer(lastRes.url().toString())
				.cookies(cookies)
				.ignoreHttpErrors(true)
				.followRedirects(false)
				.timeout(10000)
				.execute();

			cookies.putAll(lastRes.cookies());
			nextUrl = lastRes.header("Location");

			if (lastRes.url().toString().contains("portal.gachon.ac.kr/p/")) break;
			count++;
		}

		// 4단계: 포털 홈 접근 확인
		Connection.Response homeRes = Jsoup.connect(portalHome)
			.userAgent(USER_AGENT)
			.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
			.header("Accept-Language", "ko-KR,ko;q=0.9")
			.referrer(portalMain)
			.cookies(cookies)
			.ignoreHttpErrors(true)
			.followRedirects(true)
			.timeout(15000)
			.execute();

		cookies.putAll(homeRes.cookies());

		if (!homeRes.url().toString().contains("portal.gachon.ac.kr/p/")) {
			log.error("[인증오류] 포털 홈 도달 실패. 최종 url={}", homeRes.url());
			return null;
		}

		return cookies;
	}
}
