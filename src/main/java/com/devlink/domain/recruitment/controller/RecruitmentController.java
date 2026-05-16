package com.devlink.domain.recruitment.controller;

import com.devlink.domain.recruitment.dto.RecruitmentRequestDto;
import com.devlink.domain.recruitment.dto.RecruitmentResponseDto;
import com.devlink.domain.recruitment.service.RecruitmentService;
import com.devlink.global.common.ApiResponse;
import com.devlink.global.common.enums.RecruitmentStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 프로젝트 팀원 모집 컨트롤러
 *
 * @since : 2026.05.16
 * @version : 0.0.1
 * @author : DevLink Team
 */
@RestController
@RequestMapping("/api/recruitments")
@RequiredArgsConstructor
@Tag(name = "Recruitment", description = "프로젝트 팀원 모집 API")
public class RecruitmentController {

	private final RecruitmentService recruitmentService;

	/**
	 * 모집 글 작성 API
	 *
	 * @param requestDto 모집 글 작성 요청
	 * @return 작성된 모집 글
	 */
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "모집 글 작성", description = "프로젝트 팀원 모집 글을 작성합니다.")
	public ApiResponse<RecruitmentResponseDto> createRecruitment(@RequestBody RecruitmentRequestDto requestDto) {
		return ApiResponse.success("모집 글이 등록되었습니다.", recruitmentService.createRecruitment(requestDto));
	}

	/**
	 * 모집 글 전체 목록 조회 API
	 *
	 * @return 모집 글 목록
	 */
	@GetMapping
	@Operation(summary = "모집 글 목록 조회", description = "전체 프로젝트 팀원 모집 글 목록을 조회합니다.")
	public ApiResponse<List<RecruitmentResponseDto>> getAllRecruitments() {
		return ApiResponse.success(recruitmentService.getAllRecruitments());
	}

	/**
	 * 모집 글 단건 조회 API
	 *
	 * @param recruitmentId 모집 글 ID
	 * @return 모집 글 상세 정보
	 */
	@GetMapping("/{recruitmentId}")
	@Operation(summary = "모집 글 상세 조회", description = "모집 글 ID로 상세 정보를 조회합니다.")
	public ApiResponse<RecruitmentResponseDto> getRecruitmentById(@PathVariable Long recruitmentId) {
		return ApiResponse.success(recruitmentService.getRecruitmentById(recruitmentId));
	}

	/**
	 * 모집 상태 변경 API
	 *
	 * @param recruitmentId 모집 글 ID
	 * @param status 변경할 상태
	 * @return 수정된 모집 글
	 */
	@PatchMapping("/{recruitmentId}/status")
	@Operation(summary = "모집 상태 변경", description = "모집 상태를 변경합니다. (OPEN / CLOSED / DONE)")
	public ApiResponse<RecruitmentResponseDto> updateStatus(
		@PathVariable Long recruitmentId,
		@RequestParam RecruitmentStatus status
	) {
		return ApiResponse.success("모집 상태가 변경되었습니다.", recruitmentService.updateStatus(recruitmentId, status));
	}

	/**
	 * 모집 글 삭제 API
	 *
	 * @param recruitmentId 모집 글 ID
	 * @return 삭제 성공 메시지
	 */
	@DeleteMapping("/{recruitmentId}")
	@Operation(summary = "모집 글 삭제", description = "모집 글을 삭제합니다.")
	public ApiResponse<Void> deleteRecruitment(@PathVariable Long recruitmentId) {
		recruitmentService.deleteRecruitment(recruitmentId);
		return ApiResponse.success("모집 글이 삭제되었습니다.", null);
	}
}
