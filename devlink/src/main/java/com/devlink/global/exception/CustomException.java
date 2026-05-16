package com.devlink.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 커스텀 예외 클래스
 * 비즈니스 로직 예외 처리에 사용
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

	/* 공통 예외 상수 */
	public static final CustomException NOT_FOUND =
		new CustomException(HttpStatus.NOT_FOUND, "요청한 리소스를 찾을 수 없습니다.");

	public static final CustomException UNAUTHORIZED =
		new CustomException(HttpStatus.UNAUTHORIZED, "인증이 필요합니다.");

	public static final CustomException FORBIDDEN =
		new CustomException(HttpStatus.FORBIDDEN, "접근 권한이 없습니다.");

	public static final CustomException ALREADY_LIKED =
		new CustomException(HttpStatus.CONFLICT, "이미 공감한 포트폴리오입니다.");

	public static final CustomException RECRUITMENT_CLOSED =
		new CustomException(HttpStatus.BAD_REQUEST, "모집이 마감된 프로젝트입니다.");
}
