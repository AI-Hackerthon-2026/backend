package com.devlink.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * API 에러 코드 정의 Enum
 * HTTP 상태 코드와 에러 메시지를 함께 관리
 *
 * @since 2026.05.16
 * @version 1.0.0
 * @author DevLink Team
 */
@Getter
public enum ErrorCode {

	// ===== 인증 (Auth) =====
	PORTAL_AUTH_FAILED(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다."),
	PORTAL_ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 계정입니다."),
	PORTAL_CONNECTION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "포털 서버에 연결할 수 없습니다."),
	NOT_AUTHENTICATED(HttpStatus.UNAUTHORIZED, "로그인이 필요한 서비스입니다."),
	SESSION_EXPIRED(HttpStatus.UNAUTHORIZED, "세션이 만료되었습니다. 다시 로그인해주세요."),
	FIRST_LOGIN_REQUIRED(HttpStatus.UNAUTHORIZED, "먼저 로그인을 진행해주세요."),

	// ===== 사용자 (User) =====
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
	DUPLICATE_STUDENT_ID(HttpStatus.CONFLICT, "이미 사용 중인 학번입니다."),
	DUPLICATE_PORTAL_ID(HttpStatus.CONFLICT, "이미 가입된 포털 아이디입니다."),
	INVALID_GITHUB_LINK_FORMAT(HttpStatus.BAD_REQUEST, "올바른 GitHub 링크 형식을 입력해주세요."),
	INVALID_URL_FORMAT(HttpStatus.BAD_REQUEST, "올바른 URL 형식을 입력해주세요."),

	// ===== 포트폴리오 (Portfolio) =====
	PORTFOLIO_NOT_FOUND(HttpStatus.NOT_FOUND, "포트폴리오를 찾을 수 없습니다."),
	PORTFOLIO_ACCESS_DENIED(HttpStatus.FORBIDDEN, "수정 권한이 없습니다."),
	PORTFOLIO_DELETE_DENIED(HttpStatus.FORBIDDEN, "삭제 권한이 없습니다."),
	DUPLICATE_GITHUB_LINK(HttpStatus.CONFLICT, "이미 등록된 GitHub 링크입니다."),
	INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "종료일은 시작일 이후여야 합니다."),
	PARTICIPANT_REQUIRED(HttpStatus.BAD_REQUEST, "해당 카테고리는 참여자를 반드시 선택해야 합니다."),
	AWARDS_NOT_FOUND(HttpStatus.NOT_FOUND, "시상 결과가 아직 발표되지 않았습니다."),

	// ===== 공통 =====
	INVALID_INPUT(HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다."),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");

	private final HttpStatus httpStatus;
	private final String message;

	ErrorCode(HttpStatus httpStatus, String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}
}
