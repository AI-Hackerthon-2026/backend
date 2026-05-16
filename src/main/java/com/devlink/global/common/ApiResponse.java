package com.devlink.global.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * API 공통 응답 포맷
 * 모든 API 응답은 이 형식을 따른다.
 *
 * @return : { success, message, data }
 * @since : 2026.05.16
 * @version : 0.0.1
 * @author : DevLink Team
 */
@Getter
@AllArgsConstructor
public class ApiResponse<T> {

	private boolean isSuccess;
	private String message;
	private T data;

	/**
	 * 성공 응답 생성 (데이터 포함)
	 *
	 * @param data 응답 데이터
	 * @return ApiResponse 성공 객체
	 */
	public static <T> ApiResponse<T> success(T data) {
		return new ApiResponse<>(true, "요청이 성공적으로 처리되었습니다.", data);
	}

	/**
	 * 성공 응답 생성 (메시지 커스텀)
	 *
	 * @param message 성공 메시지
	 * @param data 응답 데이터
	 * @return ApiResponse 성공 객체
	 */
	public static <T> ApiResponse<T> success(String message, T data) {
		return new ApiResponse<>(true, message, data);
	}

	/**
	 * 실패 응답 생성
	 *
	 * @param message 실패 메시지
	 * @return ApiResponse 실패 객체
	 */
	public static <T> ApiResponse<T> fail(String message) {
		return new ApiResponse<>(false, message, null);
	}
}
