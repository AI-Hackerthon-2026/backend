package com.devlink.domain.auth.controller;

import com.devlink.domain.auth.dto.LoginRequest;
import com.devlink.domain.auth.dto.LoginResponse;
import com.devlink.domain.auth.dto.RegisterRequest;
import com.devlink.domain.auth.service.AuthService;
import com.devlink.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 인증 컨트롤러
 * 포털 SSO 로그인, 최초 등록, 로그아웃
 *
 * @since 2026.05.16
 * @version 1.0.0
 * @author 신태훈, 조하겸
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "인증 API")
public class AuthController {

	private final AuthService authService;

	/**
	 * 포털 SSO 로그인
	 * 기존 회원: 세션 생성 후 사용자 정보 반환
	 * 최초 로그인: isFirstLogin=true 반환 → register 호출 필요
	 */
	@PostMapping("/login")
	@Operation(summary = "포털 SSO 로그인", description = "가천대 포털 아이디/비밀번호로 로그인합니다.")
	public ResponseEntity<ApiResponse<LoginResponse>> login(
		@RequestBody @Valid LoginRequest request,
		HttpSession session) {
		LoginResponse response = authService.login(request, session);
		return ResponseEntity.ok(ApiResponse.success("로그인 성공", response));
	}

	/**
	 * 최초 등록
	 * login에서 isFirstLogin=true를 받은 경우에만 호출
	 * 임시 세션(portalId 저장)이 유효한 상태에서만 처리
	 */
	@PostMapping("/register")
	@Operation(summary = "최초 등록", description = "최초 로그인 시 학번·이름·학년을 등록합니다.")
	public ResponseEntity<ApiResponse<LoginResponse>> register(
		@RequestBody @Valid RegisterRequest request,
		HttpSession session) {
		LoginResponse response = authService.register(request, session);
		return ResponseEntity
			.status(HttpStatus.CREATED)
			.body(ApiResponse.success("가입이 완료되었습니다.", response));
	}
}
