package com.devlink.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 커스텀 예외 클래스
 * 비즈니스 로직 예외 처리에 사용
 * 정적 메서드 패턴 사용 → throw 시마다 새 인스턴스 생성으로 stacktrace 정확히 추적
 *
 * @throws : CustomException
 * @since : 2026.05.16
 * @version : 0.0.1
 * @author : DevLink Team
 */
@Getter
public class CustomException extends RuntimeException {

	private final HttpStatus status;
	private final String message;

	public CustomException(HttpStatus status, String message) {
		super(message);
		this.status = status;
		this.message = message;
	}

	/* 공통 예외 정적 팩토리 메서드 */

	/**
	 * 리소스를 찾을 수 없을 때
	 *
	 * @return CustomException 404
	 */
	public static CustomException notFound() {
		return new CustomException(HttpStatus.NOT_FOUND, "요청한 리소스를 찾을 수 없습니다.");
	}

	/**
	 * 인증이 필요할 때
	 *
	 * @return CustomException 401
	 */
	public static CustomException unauthorized() {
		return new CustomException(HttpStatus.UNAUTHORIZED, "인증이 필요합니다.");
	}

	/**
	 * 접근 권한이 없을 때
	 *
	 * @return CustomException 403
	 */
	public static CustomException forbidden() {
		return new CustomException(HttpStatus.FORBIDDEN, "접근 권한이 없습니다.");
	}

	/**
	 * 이미 공감한 포트폴리오에 재공감 시도 시
	 *
	 * @return CustomException 409
	 */
	public static CustomException alreadyLiked() {
		return new CustomException(HttpStatus.CONFLICT, "이미 공감한 포트폴리오입니다.");
	}

	/**
	 * 마감된 모집 글에 지원 시도 시
	 *
	 * @return CustomException 400
	 */
	public static CustomException recruitmentClosed() {
		return new CustomException(HttpStatus.BAD_REQUEST, "모집이 마감된 프로젝트입니다.");
	}
}
