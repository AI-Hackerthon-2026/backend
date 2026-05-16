package com.devlink.domain.portfolio.dto;

import com.devlink.domain.portfolio.entity.Portfolio;
import com.devlink.domain.portfolio.entity.PortfolioCategory;
import com.devlink.domain.portfolio.entity.PortfolioParticipant;
import com.devlink.domain.skill.entity.PortfolioSkill;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 포트폴리오 목록 응답 DTO (카드형)
 *
 * @since 2026.05.16
 * @version 1.0.0
 * @author 신태훈, 조하겸
 */
@Getter
@Builder
public class PortfolioListResponse {

	private Long id;
	private String projectName;
	private PortfolioCategory category;
	private String summary;
	private String thumbnailUrl;
	private int likeCount;
	private List<String> skills;

	/** 작성자 이름 */
	private String authorName;

	/** 참여자 수 (작성자 포함) */
	private int participantCount;

	private String githubLink;
	private String deploymentLink;
	private boolean isLiked;

	private LocalDate startDate;
	private LocalDate endDate;
	private LocalDateTime createdAt;

	public static PortfolioListResponse from(
			Portfolio portfolio,
			List<PortfolioSkill> portfolioSkills,
			List<PortfolioParticipant> participants) {
		return from(portfolio, portfolioSkills, participants, false);
	}

	public static PortfolioListResponse from(
			Portfolio portfolio,
			List<PortfolioSkill> portfolioSkills,
			List<PortfolioParticipant> participants,
			boolean isLiked) {

		return PortfolioListResponse.builder()
				.id(portfolio.getId())
				.projectName(portfolio.getProjectName())
				.category(portfolio.getCategory())
				.summary(portfolio.getSummary())
				.thumbnailUrl(portfolio.getThumbnailUrl())
				.likeCount(portfolio.getLikeCount())
				.skills(portfolioSkills.stream()
						.map(ps -> ps.getSkill().getName())
						.toList())
				.authorName(portfolio.getUser().getName())
				.participantCount(participants.size())
				.githubLink(portfolio.getGithubLink())
				.deploymentLink(portfolio.getDeploymentLink())
				.isLiked(isLiked)
				.startDate(portfolio.getStartDate())
				.endDate(portfolio.getEndDate())
				.createdAt(portfolio.getCreatedAt())
				.build();
	}
}
