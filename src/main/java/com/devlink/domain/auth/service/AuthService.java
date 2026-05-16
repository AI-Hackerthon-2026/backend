package com.devlink.domain.auth.service;

import com.devlink.domain.auth.dto.LoginRequest;
import com.devlink.domain.auth.dto.LoginResponse;
import com.devlink.domain.auth.dto.RegisterRequest;
import com.devlink.domain.user.entity.User;
import com.devlink.domain.user.entity.UserLevel;
import com.devlink.domain.user.repository.UserRepository;
import com.devlink.global.common.ValidationUtils;
import com.devlink.global.exception.CustomException;
import com.devlink.global.exception.ErrorCode;
import com.devlink.global.portal.PortalAuthService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 인증 서비스
 * 포털 SSO 로그인 → Spring Security 세션 기반 인증
 *
 * @since 2026.05.16
 * @version 1.0.0
 * @author 신태훈, 조하겸
 */
@Service
@RequiredArgsConstructor
public class AuthService {

	/* 임시 세션에 저장할 portalId 키 */
	private static final String SESSION_KEY_PORTAL_ID = "PENDING_PORTAL_ID";

	private final PortalAuthService portalAuthService;
	private final UserRepository userRepository;

	/**
	 * 포털 SSO 로그인
	 * 1. 포털 인증 수행
	 * 2. DB 사용자 조회
	 * ├─ 기존 회원 → Spring Security 정식 세션 생성
	 * └─ 최초 로그인 → 임시 세션(portalId 저장) + isFirstLogin: true 반환
	 *
	 * @param request 포털 아이디, 비밀번호
	 * @param session HTTP 세션
	 * @return 로그인 응답 (isFirstLogin 포함)
	 */
	@Transactional(readOnly = true)
	public LoginResponse login(LoginRequest request, HttpSession session) {
		// 포털 인증 수행
		boolean isAuthenticated = portalAuthService.authenticate(request.getPortalId(), request.getPassword());
		if (!isAuthenticated) {
			throw new CustomException(ErrorCode.PORTAL_AUTH_FAILED);
		}

		// DB에서 사용자 조회
		Optional<User> userOptional = userRepository.findByPortalId(request.getPortalId());

		if (userOptional.isPresent()) {
			// 기존 회원: Spring Security 정식 세션 생성
			User user = userOptional.get();
			createSecuritySession(user, session);
			return LoginResponse.ofExistingUser(user);
		} else {
			// 최초 로그인: 임시 세션에 portalId 저장
			session.setAttribute(SESSION_KEY_PORTAL_ID, request.getPortalId());
			return LoginResponse.ofFirstLogin();
		}
	}

	/**
	 * 최초 등록 처리
	 * 임시 세션에서 portalId를 꺼내 사용자를 생성하고 정식 세션으로 전환
	 *
	 * @param request 학번, 이름, 학년, userLevel
	 * @param session HTTP 세션 (임시 세션 상태여야 함)
	 * @return 등록 완료 사용자 정보
	 */
	@Transactional
	public LoginResponse register(RegisterRequest request, HttpSession session) {
		// 임시 세션에서 portalId 확인
		String portalId = (String) session.getAttribute(SESSION_KEY_PORTAL_ID);
		if (portalId == null) {
			throw new CustomException(ErrorCode.FIRST_LOGIN_REQUIRED);
		}

		// 학번 중복 확인
		if (userRepository.existsByStudentId(request.getStudentId())) {
			throw new CustomException(ErrorCode.DUPLICATE_STUDENT_ID);
		}

		// portalId 중복 확인 (혹시 동시 요청 방어)
		if (userRepository.existsByPortalId(portalId)) {
			throw new CustomException(ErrorCode.DUPLICATE_PORTAL_ID);
		}

		// GitHub 링크 형식 검증
		if (request.getGithubLink() != null && !request.getGithubLink().isBlank()
				&& !ValidationUtils.isValidGitHubLink(request.getGithubLink())) {
			throw new CustomException(ErrorCode.INVALID_GITHUB_LINK_FORMAT);
		}

		// 사용자 생성
		User newUser = User.builder()
				.portalId(portalId)
				.studentId(request.getStudentId())
				.name(request.getName())
				.grade(request.getGrade())
				.githubLink(request.getGithubLink())
				.userLevel(request.getUserLevel() != null ? request.getUserLevel() : UserLevel.STUDENT)
				.build();

		User savedUser = userRepository.save(newUser);

		// 임시 세션 속성 제거 후 정식 Spring Security 세션으로 전환
		session.removeAttribute(SESSION_KEY_PORTAL_ID);
		createSecuritySession(savedUser, session);

		return LoginResponse.ofExistingUser(savedUser);
	}

	/**
	 * Spring Security 인증 객체를 세션에 저장하는 내부 메서드
	 *
	 * @param user    인증된 사용자
	 * @param session HTTP 세션
	 */
	private void createSecuritySession(User user, HttpSession session) {
		List<SimpleGrantedAuthority> authorities = List
				.of(new SimpleGrantedAuthority("ROLE_" + user.getUserLevel().name()));

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user.getId(), null,
				authorities);

		SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
		securityContext.setAuthentication(authentication);
		SecurityContextHolder.setContext(securityContext);

		session.setAttribute(
				HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
				securityContext);
	}
}
