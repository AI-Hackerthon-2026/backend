package com.devlink.domain.portfolio.dto;

import com.devlink.domain.portfolio.entity.Portfolio;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 포트폴리오 응답 DTO
 *
 * @return : 포트폴리오 정보
 * @since : 2026.05.16
 * @version : 0.0.1
 * @author : DevLink Team
 */
@Getter
@Builder
public class PortfolioResponseDto {

	private Long id;
	private String title;
	private String summary;
	private String description;
	private String thumbnailUrl;
	private String githubUrl;
	private String deployUrl;
	private String techStack;
	private int teamSize;
	private String myRole;
	private LocalDate startDate;
	private LocalDate endDate;
	private int likeCount;
	private Long userId;
	private String userName;
	private LocalDateTime createdAt;

	/**
	 * Portfolio 엔티티 → PortfolioResponseDto 변환
	 *
	 * @param portfolio Portfolio 엔티티
	 * @return PortfolioResponseDto
	 */
	public static PortfolioResponseDto from(Portfolio portfolio) {
		return PortfolioResponseDto.builder()
			.id(portfolio.getId())
			.title(portfolio.getTitle())
			.summary(portfolio.getSummary())
			.description(portfolio.getDescription())
			.thumbnailUrl(portfolio.getThumbnailUrl())
			.githubUrl(portfolio.getGithubUrl())
			.deployUrl(portfolio.getDeployUrl())
			.techStack(portfolio.getTechStack())
			.teamSize(portfolio.getTeamSize())
			.myRole(portfolio.getMyRole())
			.startDate(portfolio.getStartDate())
			.endDate(portfolio.getEndDate())
			.likeCount(portfolio.getLikeCount())
			.userId(portfolio.getUser().getId())
			.userName(portfolio.getUser().getName())
			.createdAt(portfolio.getCreatedAt())
			.build();
	}
}
