package com.devlink.domain.skill.entity;

import com.devlink.domain.portfolio.entity.Portfolio;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 포트폴리오-기술 매핑 엔티티 (N:M 중간 테이블)
 * DB_Schema.md portfolio_skills 테이블 기준
 *
 * @since 2026.05.16
 * @version 1.0.0
 * @author 신태훈, 조하겸
 */
@Entity
@Table(name = "portfolio_skills",
	uniqueConstraints = @UniqueConstraint(
		name = "uk_portfolio_skill",
		columnNames = {"portfolio_id", "skill_id"}))
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class PortfolioSkill {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "portfolio_id", nullable = false)
	private Portfolio portfolio;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "skill_id", nullable = false)
	private Skill skill;

	@CreatedDate
	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	@Builder
	public PortfolioSkill(Portfolio portfolio, Skill skill) {
		this.portfolio = portfolio;
		this.skill = skill;
	}
}
