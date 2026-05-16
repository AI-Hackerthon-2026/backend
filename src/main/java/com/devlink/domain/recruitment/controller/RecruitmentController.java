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
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/api/recruitments")
@RequiredArgsConstructor
@Tag(name = "Recruitment", description = "프로젝트 팀원 모집 API")
public class RecruitmentController {

	private final RecruitmentService recruitmentService;

	String JsonView = "jsonView";

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "모집 글 작성", description = "프로젝트 팀원 모집 글을 작성합니다.")
	public ModelAndView createRecruitment(@RequestBody RecruitmentRequestDto requestDto) {
		ModelAndView modelAndView = new ModelAndView(JsonView);
		modelAndView.addObject("data", ApiResponse.success("모집 글이 등록되었습니다.", recruitmentService.createRecruitment(requestDto)));
		return modelAndView;
	}

	@GetMapping
	@Operation(summary = "모집 글 목록 조회", description = "전체 프로젝트 팀원 모집 글 목록을 조회합니다.")
	public ModelAndView getAllRecruitments() {
		ModelAndView modelAndView = new ModelAndView(JsonView);
		modelAndView.addObject("data", ApiResponse.success(recruitmentService.getAllRecruitments()));
		return modelAndView;
	}

	@GetMapping("/{recruitmentId}")
	@Operation(summary = "모집 글 상세 조회", description = "모집 글 ID로 상세 정보를 조회합니다.")
	public ModelAndView getRecruitmentById(@PathVariable Long recruitmentId) {
		ModelAndView modelAndView = new ModelAndView(JsonView);
		modelAndView.addObject("data", ApiResponse.success(recruitmentService.getRecruitmentById(recruitmentId)));
		return modelAndView;
	}

	@PatchMapping("/{recruitmentId}/status")
	@Operation(summary = "모집 상태 변경", description = "모집 상태를 변경합니다. (OPEN / CLOSED / DONE)")
	public ModelAndView updateStatus(
		@PathVariable Long recruitmentId,
		@RequestParam RecruitmentStatus status
	) {
		ModelAndView modelAndView = new ModelAndView(JsonView);
		modelAndView.addObject("data", ApiResponse.success("모집 상태가 변경되었습니다.", recruitmentService.updateStatus(recruitmentId, status)));
		return modelAndView;
	}

	@DeleteMapping("/{recruitmentId}")
	@Operation(summary = "모집 글 삭제", description = "모집 글을 삭제합니다.")
	public ModelAndView deleteRecruitment(@PathVariable Long recruitmentId) {
		recruitmentService.deleteRecruitment(recruitmentId);
		ModelAndView modelAndView = new ModelAndView(JsonView);
		modelAndView.addObject("data", ApiResponse.success("모집 글이 삭제되었습니다.", null));
		return modelAndView;
	}
}