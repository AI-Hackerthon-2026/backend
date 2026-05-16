package com.devlink.domain.portfolio.controller;

import com.devlink.domain.portfolio.dto.PortfolioRequestDto;
import com.devlink.domain.portfolio.dto.PortfolioResponseDto;
import com.devlink.domain.portfolio.service.PortfolioService;
import com.devlink.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/api/portfolios")
@RequiredArgsConstructor
@Tag(name = "Portfolio", description = "포트폴리오 API")
public class PortfolioController {

	private final PortfolioService portfolioService;

	String JsonView = "jsonView";

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "포트폴리오 등록", description = "새로운 포트폴리오를 등록합니다.")
	public ModelAndView createPortfolio(@RequestBody PortfolioRequestDto requestDto) {
		ModelAndView modelAndView = new ModelAndView(JsonView);
		modelAndView.addObject("data", ApiResponse.success("포트폴리오가 등록되었습니다.", portfolioService.createPortfolio(requestDto)));
		return modelAndView;
	}

	@GetMapping
	@Operation(summary = "포트폴리오 목록 조회", description = "전체 포트폴리오 목록을 조회합니다.")
	public ModelAndView getAllPortfolios() {
		ModelAndView modelAndView = new ModelAndView(JsonView);
		modelAndView.addObject("data", ApiResponse.success(portfolioService.getAllPortfolios()));
		return modelAndView;
	}

	@GetMapping("/{portfolioId}")
	@Operation(summary = "포트폴리오 상세 조회", description = "포트폴리오 ID로 상세 정보를 조회합니다.")
	public ModelAndView getPortfolioById(@PathVariable Long portfolioId) {
		ModelAndView modelAndView = new ModelAndView(JsonView);
		modelAndView.addObject("data", ApiResponse.success(portfolioService.getPortfolioById(portfolioId)));
		return modelAndView;
	}

	@GetMapping("/ranking")
	@Operation(summary = "포트폴리오 랭킹 조회", description = "공감 수 기준 포트폴리오 랭킹을 조회합니다.")
	public ModelAndView getRanking() {
		ModelAndView modelAndView = new ModelAndView(JsonView);
		modelAndView.addObject("data", ApiResponse.success(portfolioService.getRanking()));
		return modelAndView;
	}

	@DeleteMapping("/{portfolioId}")
	@Operation(summary = "포트폴리오 삭제", description = "포트폴리오를 삭제합니다.")
	public ModelAndView deletePortfolio(@PathVariable Long portfolioId) {
		portfolioService.deletePortfolio(portfolioId);
		ModelAndView modelAndView = new ModelAndView(JsonView);
		modelAndView.addObject("data", ApiResponse.success("포트폴리오가 삭제되었습니다.", null));
		return modelAndView;
	}
}