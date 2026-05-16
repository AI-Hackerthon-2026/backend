package com.devlink.domain.like.controller;

import com.devlink.domain.like.dto.LikeResponse;
import com.devlink.domain.like.service.LikeService;
import com.devlink.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 공감 컨트롤러
 *
 * @since 2026.05.16
 * @version 1.0.0
 * @author 신태훈, 조하겸
 */
@RestController
@RequestMapping("/api/portfolios")
@RequiredArgsConstructor
@Tag(name = "Like", description = "공감 API")
public class LikeController {

	private final LikeService likeService;

	/**
	 * 공감 토글
	 * 공감 추가 또는 취소
	 */
	@PostMapping("/{id}/likes")
	@Operation(summary = "공감 토글", description = "포트폴리오에 공감을 추가하거나 취소합니다.")
	public ResponseEntity<ApiResponse<LikeResponse>> toggleLike(
		@PathVariable Long id,
		Authentication authentication) {
		Long userId = (Long) authentication.getPrincipal();
		LikeResponse response = likeService.toggleLike(id, userId);
		String message = response.isLiked() ? "공감을 추가했습니다." : "공감을 취소했습니다.";
		return ResponseEntity.ok(ApiResponse.success(message, response));
	}
}
