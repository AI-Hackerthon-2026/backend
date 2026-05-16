package com.devlink.domain.portfolio.controller;

import com.devlink.domain.portfolio.dto.*;
import com.devlink.domain.portfolio.service.PortfolioService;
import com.devlink.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 포트폴리오 컨트롤러
 * 포트폴리오 CRUD, TOP3, 랭킹, 시상 결과
 *
 * @since 2026.05.16
 * @version 1.0.0
 * @author 신태훈, 조하겸
 */
@RestController
@RequestMapping("/api/portfolios")
@RequiredArgsConstructor
@Tag(name = "Portfolio", description = "포트폴리오 API")
public class PortfolioController {

	private final PortfolioService portfolioService;

	/**
	 * 전체 목록 조회
	 * skills 파라미터로 필터, sort로 정렬 (LATEST/LIKES)
	 */
	@GetMapping
	@Operation(summary = "포트폴리오 목록 조회", description = "기술스택 필터 및 정렬을 적용하여 목록을 조회합니다.")
	public ResponseEntity<ApiResponse<List<PortfolioListResponse>>> getPortfolioList(
		@RequestParam(required = false) List<String> skills,
		@RequestParam(required = false, defaultValue = "LATEST") String sort) {
		return ResponseEntity.ok(
			ApiResponse.success("포트폴리오 목록 조회 성공", portfolioService.getPortfolioList(skills, sort)));
	}

	/**
	 * 포트폴리오 작성 (인증 필요)
	 */
	@PostMapping
	@Operation(summary = "포트폴리오 작성", description = "새 포트폴리오를 등록합니다.")
	public ResponseEntity<ApiResponse<PortfolioDetailResponse>> createPortfolio(
		@RequestBody @Valid PortfolioCreateRequest request,
		Authentication authentication) {
		Long userId = (Long) authentication.getPrincipal();
		PortfolioDetailResponse response = portfolioService.createPortfolio(request, userId);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.success("포트폴리오 등록 완료", response));
	}

	/**
	 * 메인 인기 TOP 3 조회
	 */
	@GetMapping("/top")
	@Operation(summary = "인기 TOP 3", description = "공감 수 기준 상위 3개 포트폴리오를 조회합니다.")
	public ResponseEntity<ApiResponse<List<PortfolioListResponse>>> getTop3() {
		return ResponseEntity.ok(ApiResponse.success("TOP 3 조회 성공", portfolioService.getTop3()));
	}

	/**
	 * 랭킹 목록 조회 (공감 많은 순)
	 */
	@GetMapping("/ranking")
	@Operation(summary = "랭킹 목록", description = "공감 수 기준 랭킹 목록을 조회합니다.")
	public ResponseEntity<ApiResponse<List<PortfolioListResponse>>> getRanking() {
		return ResponseEntity.ok(ApiResponse.success("랭킹 조회 성공", portfolioService.getRanking()));
	}

	/**
	 * 시상 결과 조회 (GRADUATION 카테고리 기준 상위)
	 */
	@GetMapping("/awards")
	@Operation(summary = "시상 결과", description = "졸업 프로젝트 시상 결과를 조회합니다.")
	public ResponseEntity<ApiResponse<List<PortfolioListResponse>>> getAwards() {
		/* 졸업 카테고리 필터 + 공감 순 정렬 */
		List<PortfolioListResponse> all = portfolioService.getPortfolioList(null, "LIKES");
		List<PortfolioListResponse> awards = all.stream()
			.filter(p -> p.getCategory().name().equals("GRADUATION"))
			.limit(10)
			.toList();
		return ResponseEntity.ok(ApiResponse.success("시상 결과 조회 성공", awards));
	}

	/**
	 * 포트폴리오 상세 조회
	 */
	@GetMapping("/{id}")
	@Operation(summary = "포트폴리오 상세 조회", description = "포트폴리오 상세 정보를 조회합니다.")
	public ResponseEntity<ApiResponse<PortfolioDetailResponse>> getPortfolioDetail(
		@PathVariable Long id,
		Authentication authentication) {
		/* 비로그인 사용자는 userId null */
		Long userId = authentication != null ? (Long) authentication.getPrincipal() : null;
		return ResponseEntity.ok(
			ApiResponse.success("상세 조회 성공", portfolioService.getPortfolioDetail(id, userId)));
	}

	/**
	 * 포트폴리오 수정 (can_edit=true 참여자만)
	 */
	@PutMapping("/{id}")
	@Operation(summary = "포트폴리오 수정", description = "수정 권한이 있는 참여자가 포트폴리오를 수정합니다.")
	public ResponseEntity<ApiResponse<PortfolioDetailResponse>> updatePortfolio(
		@PathVariable Long id,
		@RequestBody @Valid PortfolioUpdateRequest request,
		Authentication authentication) {
		Long userId = (Long) authentication.getPrincipal();
		return ResponseEntity.ok(
			ApiResponse.success("포트폴리오 수정 완료", portfolioService.updatePortfolio(id, request, userId)));
	}

	/**
	 * 포트폴리오 삭제 (is_owner=true 작성자만)
	 */
	@DeleteMapping("/{id}")
	@Operation(summary = "포트폴리오 삭제", description = "소유자만 포트폴리오를 삭제할 수 있습니다.")
	public ResponseEntity<ApiResponse<Void>> deletePortfolio(
		@PathVariable Long id,
		Authentication authentication) {
		Long userId = (Long) authentication.getPrincipal();
		portfolioService.deletePortfolio(id, userId);
		return ResponseEntity.ok(ApiResponse.success("포트폴리오 삭제 완료"));
	}
}
