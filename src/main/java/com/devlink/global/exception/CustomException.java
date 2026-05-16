package com.devlink.global.exception;

import lombok.Getter;

/**
 * 커스텀 예외 클래스
 * ErrorCode를 기반으로 예외를 발생시킴
 *
 * @since 2026.05.16
 * @version 1.0.0
 * @author DevLink Team
 */
@Getter
public class CustomException extends RuntimeException {

	private final ErrorCode errorCode;

	public CustomException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}
}
