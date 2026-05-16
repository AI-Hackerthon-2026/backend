package com.devlink.domain.like.controller;

import com.devlink.domain.like.service.LikeService;
import com.devlink.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/api/portfolios/{portfolioId}/likes")
@RequiredArgsConstructor
@Tag(name = "Like", description = "포트폴리오 공감 API")
public class LikeController {

	private final LikeService likeService;

	String JsonView = "jsonView";

	@PostMapping
	@Operation(summary = "공감 추가", description = "포트폴리오에 공감을 추가합니다.")
	public ModelAndView addLike(
		@PathVariable Long portfolioId,
		@RequestParam Long userId
	) {
		likeService.addLike(userId, portfolioId);
		ModelAndView modelAndView = new ModelAndView(JsonView);
		modelAndView.addObject("data", ApiResponse.success("공감이 추가되었습니다.", null));
		return modelAndView;
	}

	@DeleteMapping
	@Operation(summary = "공감 취소", description = "포트폴리오 공감을 취소합니다.")
	public ModelAndView cancelLike(
		@PathVariable Long portfolioId,
		@RequestParam Long userId
	) {
		likeService.cancelLike(userId, portfolioId);
		ModelAndView modelAndView = new ModelAndView(JsonView);
		modelAndView.addObject("data", ApiResponse.success("공감이 취소되었습니다.", null));
		return modelAndView;
	}
}
