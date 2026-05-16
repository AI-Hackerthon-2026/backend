package com.devlink.domain.user.controller;

import com.devlink.domain.portfolio.dto.PortfolioListResponse;
import com.devlink.domain.portfolio.service.PortfolioService;
import com.devlink.domain.user.dto.UserProfileResponse;
import com.devlink.domain.user.dto.UserSearchResponse;
import com.devlink.domain.user.dto.UserUpdateRequest;
import com.devlink.domain.user.service.UserService;
import com.devlink.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 사용자 컨트롤러
 * 내 프로필 조회 및 수정
 *
 * @since 2026.05.16
 * @version 1.0.0
 * @author 신태훈, 조하겸
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "사용자 마이페이지 API")
public class UserController {

	private final UserService userService;
	private final PortfolioService portfolioService;

	/**
	 * 내 프로필 조회
	 * Spring Security Authentication에서 userId(principal) 추출
	 */
	@GetMapping("/me")
	@Operation(summary = "내 프로필 조회", description = "현재 로그인된 사용자의 프로필을 조회합니다.")
	public ResponseEntity<ApiResponse<UserProfileResponse>> getMyProfile(Authentication authentication) {
		Long userId = (Long) authentication.getPrincipal();
		UserProfileResponse response = userService.getMyProfile(userId);
		return ResponseEntity.ok(ApiResponse.success("프로필 조회 성공", response));
	}

	/**
	 * 내 프로필 수정 (name, grade, githubLink만 수정 가능)
	 */
	@PatchMapping("/me")
	@Operation(summary = "내 프로필 수정", description = "이름, 학년, GitHub 링크를 수정합니다.")
	public ResponseEntity<ApiResponse<UserProfileResponse>> updateMyProfile(
		@RequestBody @Valid UserUpdateRequest request,
		Authentication authentication) {
		Long userId = (Long) authentication.getPrincipal();
		UserProfileResponse response = userService.updateMyProfile(userId, request);
		return ResponseEntity.ok(ApiResponse.success("프로필 수정 성공", response));
	}

	/**
	 * 내 포트폴리오 목록 조회 (참여자로 등록된 포트폴리오 포함)
	 */
	@GetMapping("/me/portfolios")
	@Operation(summary = "내 포트폴리오 목록", description = "내가 작성하거나 참여한 포트폴리오 목록을 조회합니다.")
	public ResponseEntity<ApiResponse<List<PortfolioListResponse>>> getMyPortfolios(
		Authentication authentication) {
		Long userId = (Long) authentication.getPrincipal();
		return ResponseEntity.ok(
			ApiResponse.success("내 포트폴리오 목록 조회 성공", portfolioService.getMyPortfolios(userId)));
	}

	/**
	 * 사용자 검색 (참여자 추가용)
	 * 이름 또는 학번으로 검색, 본인 제외
	 */
	@GetMapping("/search")
	@Operation(summary = "사용자 검색", description = "이름 또는 학번으로 사용자를 검색합니다.")
	public ResponseEntity<ApiResponse<List<UserSearchResponse>>> searchUsers(
		@RequestParam String q,
		Authentication authentication) {
		Long userId = (Long) authentication.getPrincipal();
		return ResponseEntity.ok(
			ApiResponse.success("사용자 검색 성공", userService.searchUsers(q, userId)));
	}
}
