package com.devlink.domain.skill.dto;

import com.devlink.domain.skill.entity.Skill;
import lombok.Builder;
import lombok.Getter;

/**
 * 기술스택 응답 DTO
 *
 * @since 2026.05.16
 * @version 1.0.0
 * @author 신태훈, 조하겸
 */
@Getter
@Builder
public class SkillResponse {

	private Long id;
	private String name;
	private String category;

	public static SkillResponse from(Skill skill) {
		return SkillResponse.builder()
			.id(skill.getId())
			.name(skill.getName())
			.category(skill.getCategory())
			.build();
	}
}
