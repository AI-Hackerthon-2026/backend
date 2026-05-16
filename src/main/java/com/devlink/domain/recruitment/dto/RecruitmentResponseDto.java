package com.devlink.domain.recruitment.dto;

import com.devlink.domain.recruitment.entity.Recruitment;
import com.devlink.global.common.enums.RecruitmentStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 프로젝트 팀원 모집 응답 DTO
 *
 * @return : 모집 글 정보
 * @since : 2026.05.16
 * @version : 0.0.1
 * @author : DevLink Team
 */
@Getter
@Builder
public class RecruitmentResponseDto {

	private Long id;
	private String title;
	private String description;
	private String roles;
	private int maxMembers;
	private String techStack;
	private LocalDate deadline;
	private RecruitmentStatus status;
	private String contactMethod;
	private Long userId;
	private String userName;
	private LocalDateTime createdAt;

	/**
	 * Recruitment 엔티티 → RecruitmentResponseDto 변환
	 *
	 * @param recruitment Recruitment 엔티티
	 * @return RecruitmentResponseDto
	 */
	public static RecruitmentResponseDto from(Recruitment recruitment) {
		return RecruitmentResponseDto.builder()
			.id(recruitment.getId())
			.title(recruitment.getTitle())
			.description(recruitment.getDescription())
			.roles(recruitment.getRoles())
			.maxMembers(recruitment.getMaxMembers())
			.techStack(recruitment.getTechStack())
			.deadline(recruitment.getDeadline())
			.status(recruitment.getStatus())
			.contactMethod(recruitment.getContactMethod())
			.userId(recruitment.getUser().getId())
			.userName(recruitment.getUser().getName())
			.createdAt(recruitment.getCreatedAt())
			.build();
	}
}
