package com.devlink.domain.skill.controller;

import com.devlink.domain.skill.dto.SkillResponse;
import com.devlink.domain.skill.service.SkillService;
import com.devlink.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 기술스택 컨트롤러
 *
 * @since 2026.05.16
 * @version 1.0.0
 * @author 신태훈, 조하겸
 */
@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
@Tag(name = "Skill", description = "기술스택 API")
public class SkillController {

	private final SkillService skillService;

	/**
	 * 기술스택 전체 목록 조회 (자동완성용)
	 *
	 * @param q        검색어 (선택)
	 * @param category 카테고리 필터 (선택)
	 */
	@GetMapping
	@Operation(summary = "기술스택 목록 조회", description = "기술스택 자동완성을 위한 목록을 조회합니다.")
	public ResponseEntity<ApiResponse<List<SkillResponse>>> getSkills(
		@RequestParam(required = false) String q,
		@RequestParam(required = false) String category) {
		return ResponseEntity.ok(ApiResponse.success("기술스택 목록 조회 성공", skillService.getSkills(q, category)));
	}
}
