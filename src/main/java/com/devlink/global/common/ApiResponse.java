package com.devlink.global.common;

import lombok.Getter;

/**
 * 공통 API 응답 래퍼 클래스
 * 모든 API 응답은 이 형식을 따름
 *
 * @param <T> 응답 데이터 타입
 * @since 2026.05.16
 * @version 1.0.0
 * @author DevLink Team
 */
@Getter
public class ApiResponse<T> {

	private final boolean success;
	private final String message;
	private final T data;

	private ApiResponse(boolean success, String message, T data) {
		this.success = success;
		this.message = message;
		this.data = data;
	}

	/**
	 * 성공 응답 생성 (데이터 포함)
	 */
	public static <T> ApiResponse<T> success(String message, T data) {
		return new ApiResponse<>(true, message, data);
	}

	/**
	 * 성공 응답 생성 (데이터 없음)
	 */
	public static <T> ApiResponse<T> success(String message) {
		return new ApiResponse<>(true, message, null);
	}

	/**
	 * 실패 응답 생성
	 */
	public static <T> ApiResponse<T> fail(String message) {
		return new ApiResponse<>(false, message, null);
	}
}
