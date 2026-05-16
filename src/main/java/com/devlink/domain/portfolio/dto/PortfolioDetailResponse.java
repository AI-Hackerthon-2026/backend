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
 * 포트폴리오 상세 조회 응답 DTO
 *
 * @since 2026.05.16
 * @version 1.0.0
 * @author 신태훈, 조하겸
 */
@Getter
@Builder
public class PortfolioDetailResponse {

	private Long id;
	private String projectName;
	private PortfolioCategory category;
	private String summary;
	private String description;
	private String thumbnailUrl;
	private String imageUrl;
	private String githubLink;
	private String deploymentLink;
	private int likeCount;
	private List<String> skills;
	private List<ParticipantInfo> participants;
	private LocalDate startDate;
	private LocalDate endDate;
	private LocalDateTime createdAt;

	/** 현재 로그인 사용자의 수정 권한 여부 */
	private boolean canEdit;

	/** 현재 로그인 사용자의 소유자 여부 */
	private boolean isOwner;

	/** 현재 로그인 사용자의 공감 여부 */
	private boolean isLiked;

	/**
	 * 참여자 정보 내부 DTO
	 */
	@Getter
	@Builder
	public static class ParticipantInfo {
		private Long userId;
		private String name;
		private String role;
		private boolean isOwner;
	}

	public static PortfolioDetailResponse from(
		Portfolio portfolio,
		List<PortfolioSkill> portfolioSkills,
		List<PortfolioParticipant> participants,
		boolean canEdit,
		boolean isOwner,
		boolean isLiked) {

		return PortfolioDetailResponse.builder()
			.id(portfolio.getId())
			.projectName(portfolio.getProjectName())
			.category(portfolio.getCategory())
			.summary(portfolio.getSummary())
			.description(portfolio.getDescription())
			.thumbnailUrl(portfolio.getThumbnailUrl())
			.imageUrl(portfolio.getImageUrl())
			.githubLink(portfolio.getGithubLink())
			.deploymentLink(portfolio.getDeploymentLink())
			.likeCount(portfolio.getLikeCount())
			.skills(portfolioSkills.stream()
				.map(ps -> ps.getSkill().getName())
				.toList())
			.participants(participants.stream()
				.map(p -> ParticipantInfo.builder()
					.userId(p.getUser().getId())
					.name(p.getUser().getName())
					.role(p.getRole())
					.isOwner(p.isOwner())
					.build())
				.toList())
			.startDate(portfolio.getStartDate())
			.endDate(portfolio.getEndDate())
			.createdAt(portfolio.getCreatedAt())
			.canEdit(canEdit)
			.isOwner(isOwner)
			.isLiked(isLiked)
			.build();
	}
}
