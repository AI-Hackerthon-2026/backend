package com.devlink.domain.like.controller;

import com.devlink.domain.like.service.LikeService;
import com.devlink.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 포트폴리오 공감 컨트롤러
 *
 * @since : 2026.05.16
 * @version : 0.0.1
 * @author : DevLink Team
 */
@RestController
@RequestMapping("/api/portfolios/{portfolioId}/likes")
@RequiredArgsConstructor
@Tag(name = "Like", description = "포트폴리오 공감 API")
public class LikeController {

	private final LikeService likeService;

	/**
	 * 공감 추가 API
	 *
	 * @param portfolioId 포트폴리오 ID
	 * @param userId 사용자 ID
	 * @return 공감 추가 성공 메시지
	 */
	@PostMapping
	@Operation(summary = "공감 추가", description = "포트폴리오에 공감을 추가합니다.")
	public ApiResponse<Void> addLike(
		@PathVariable Long portfolioId,
		@RequestParam Long userId
	) {
		likeService.addLike(userId, portfolioId);
		return ApiResponse.success("공감이 추가되었습니다.", null);
	}

	/**
	 * 공감 취소 API
	 *
	 * @param portfolioId 포트폴리오 ID
	 * @param userId 사용자 ID
	 * @return 공감 취소 성공 메시지
	 */
	@DeleteMapping
	@Operation(summary = "공감 취소", description = "포트폴리오 공감을 취소합니다.")
	public ApiResponse<Void> cancelLike(
		@PathVariable Long portfolioId,
		@RequestParam Long userId
	) {
		likeService.cancelLike(userId, portfolioId);
		return ApiResponse.success("공감이 취소되었습니다.", null);
	}
}
