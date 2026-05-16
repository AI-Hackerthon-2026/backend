package com.devlink.domain.portfolio.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 포트폴리오 요청 DTO
 *
 * @since : 2026.05.16
 * @version : 0.0.1
 * @author : DevLink Team
 */
@Getter
@NoArgsConstructor
public class PortfolioRequestDto {

	/** 프로젝트명 */
	private String title;

	/** 프로젝트 요약 */
	private String summary;

	/** 프로젝트 상세 설명 */
	private String description;

	/** 대표 이미지 URL */
	private String thumbnailUrl;

	/** GitHub 링크 */
	private String githubUrl;

	/** 배포 링크 */
	private String deployUrl;

	/** 사용 기술 스택 */
	private String techStack;

	/** 참여 인원 */
	private int teamSize;

	/** 담당 역할 */
	private String myRole;

	/** 개발 시작일 */
	private LocalDate startDate;

	/** 개발 종료일 */
	private LocalDate endDate;

	/** 작성자 ID */
	private Long userId;
}
