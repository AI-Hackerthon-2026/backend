package com.devlink.global.exception;

import com.devlink.global.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 전역 예외 처리 핸들러
 * 모든 컨트롤러에서 발생하는 예외를 통합 처리
 *
 * @since : 2026.05.16
 * @version : 0.0.1
 * @author : DevLink Team
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * CustomException 처리
	 *
	 * @param e 커스텀 예외
	 * @return 에러 ApiResponse
	 */
	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ApiResponse<Void>> handleCustomException(CustomException e) {
		return ResponseEntity
			.status(e.getStatus())
			.body(ApiResponse.fail(e.getMessage()));
	}

	/**
	 * 그 외 RuntimeException 처리
	 *
	 * @param e 런타임 예외
	 * @return 서버 에러 ApiResponse
	 */
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ApiResponse<Void>> handleRuntimeException(RuntimeException e) {
		return ResponseEntity
			.status(500)
			.body(ApiResponse.fail("서버 내부 오류가 발생했습니다."));
	}
}
