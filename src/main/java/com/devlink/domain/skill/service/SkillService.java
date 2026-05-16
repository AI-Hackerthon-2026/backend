package com.devlink.domain.skill.service;

import com.devlink.domain.skill.dto.SkillResponse;
import com.devlink.domain.skill.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 기술스택 서비스
 * 전체 목록 조회 및 자동완성 검색
 *
 * @since 2026.05.16
 * @version 1.0.0
 * @author 신태훈, 조하겸
 */
@Service
@RequiredArgsConstructor
public class SkillService {

	private final SkillRepository skillRepository;

	/**
	 * 기술스택 목록 조회 (자동완성용)
	 * q: 이름 검색, category: 카테고리 필터
	 *
	 * @param q        검색어 (null이면 전체)
	 * @param category 카테고리 필터 (null이면 전체)
	 * @return 기술스택 목록
	 */
	@Transactional(readOnly = true)
	public List<SkillResponse> getSkills(String q, String category) {
		if (q != null && category != null) {
			return skillRepository.findByNameContainingIgnoreCaseAndCategory(q, category)
				.stream().map(SkillResponse::from).toList();
		} else if (q != null) {
			return skillRepository.findByNameContainingIgnoreCase(q)
				.stream().map(SkillResponse::from).toList();
		} else if (category != null) {
			return skillRepository.findByCategory(category)
				.stream().map(SkillResponse::from).toList();
		}
		return skillRepository.findAll()
			.stream().map(SkillResponse::from).toList();
	}
}
