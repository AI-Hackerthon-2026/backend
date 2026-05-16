package com.devlink.domain.recruitment.dto;

import com.devlink.global.common.enums.RecruitmentStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 프로젝트 팀원 모집 요청 DTO
 *
 * @since : 2026.05.16
 * @version : 0.0.1
 * @author : DevLink Team
 */
@Getter
@NoArgsConstructor
public class RecruitmentRequestDto {

	/** 프로젝트 제목 */
	private String title;

	/** 프로젝트 소개 */
	private String description;

	/** 모집 역할 */
	private String roles;

	/** 모집 인원 */
	private int maxMembers;

	/** 기술 스택 */
	private String techStack;

	/** 모집 마감일 */
	private LocalDate deadline;

	/** 연락/지원 방식 */
	private String contactMethod;

	/** 작성자 ID */
	private Long userId;
}
