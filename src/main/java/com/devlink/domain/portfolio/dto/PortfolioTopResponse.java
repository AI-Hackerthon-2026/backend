package com.devlink.domain.portfolio.dto;

import com.devlink.domain.portfolio.entity.Portfolio;
import com.devlink.domain.skill.entity.PortfolioSkill;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 메인 배너용 TOP 포트폴리오 응답 DTO
 *
 * @since 2026.05.17
 * @version 1.0.0
 */
@Getter
@Builder
public class PortfolioTopResponse {

    private String period;
    private String periodLabel;
    private Long id;
    private String projectName;
    private String summary;
    private String authorName;
    private List<String> skills;
    private int likeCount;
    private String thumbnailUrl;

    public static PortfolioTopResponse from(
            Portfolio portfolio,
            List<PortfolioSkill> portfolioSkills,
            String period,
            String periodLabel) {
        return PortfolioTopResponse.builder()
                .period(period)
                .periodLabel(periodLabel)
                .id(portfolio.getId())
                .projectName(portfolio.getProjectName())
                .summary(portfolio.getSummary())
                .authorName(portfolio.getUser().getName())
                .skills(portfolioSkills.stream()
                        .map(ps -> ps.getSkill().getName())
                        .toList())
                .likeCount(portfolio.getLikeCount())
                .thumbnailUrl(portfolio.getThumbnailUrl())
                .build();
    }
}
