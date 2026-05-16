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

import java.util.List;

/**
 * 포트폴리오 컨트롤러
 *
 * @since : 2026.05.16
 * @version : 0.0.1
 * @author : DevLink Team
 */
@RestController
@RequestMapping("/api/portfolios")
@RequiredArgsConstructor
@Tag(name = "Portfolio", description = "포트폴리오 API")
public class PortfolioController {

	private final PortfolioService portfolioService;

	/**
	 * 포트폴리오 등록 API
	 *
	 * @param requestDto 포트폴리오 등록 요청
	 * @return 등록된 포트폴리오
	 */
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "포트폴리오 등록", description = "새로운 포트폴리오를 등록합니다.")
	public ApiResponse<PortfolioResponseDto> createPortfolio(@RequestBody PortfolioRequestDto requestDto) {
		return ApiResponse.success("포트폴리오가 등록되었습니다.", portfolioService.createPortfolio(requestDto));
	}

	/**
	 * 포트폴리오 전체 목록 조회 API
	 *
	 * @return 포트폴리오 목록
	 */
	@GetMapping
	@Operation(summary = "포트폴리오 목록 조회", description = "전체 포트폴리오 목록을 조회합니다.")
	public ApiResponse<List<PortfolioResponseDto>> getAllPortfolios() {
		return ApiResponse.success(portfolioService.getAllPortfolios());
	}

	/**
	 * 포트폴리오 단건 조회 API
	 *
	 * @param portfolioId 포트폴리오 ID
	 * @return 포트폴리오 상세 정보
	 */
	@GetMapping("/{portfolioId}")
	@Operation(summary = "포트폴리오 상세 조회", description = "포트폴리오 ID로 상세 정보를 조회합니다.")
	public ApiResponse<PortfolioResponseDto> getPortfolioById(@PathVariable Long portfolioId) {
		return ApiResponse.success(portfolioService.getPortfolioById(portfolioId));
	}

	/**
	 * 공감 수 기반 랭킹 조회 API
	 *
	 * @return 랭킹 순 포트폴리오 목록
	 */
	@GetMapping("/ranking")
	@Operation(summary = "포트폴리오 랭킹 조회", description = "공감 수 기준 포트폴리오 랭킹을 조회합니다.")
	public ApiResponse<List<PortfolioResponseDto>> getRanking() {
		return ApiResponse.success(portfolioService.getRanking());
	}

	/**
	 * 포트폴리오 삭제 API
	 *
	 * @param portfolioId 포트폴리오 ID
	 * @return 삭제 성공 메시지
	 */
	@DeleteMapping("/{portfolioId}")
	@Operation(summary = "포트폴리오 삭제", description = "포트폴리오를 삭제합니다.")
	public ApiResponse<Void> deletePortfolio(@PathVariable Long portfolioId) {
		portfolioService.deletePortfolio(portfolioId);
		return ApiResponse.success("포트폴리오가 삭제되었습니다.", null);
	}
}
