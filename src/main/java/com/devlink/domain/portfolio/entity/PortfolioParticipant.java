package com.devlink.domain.portfolio.entity;

import com.devlink.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 포트폴리오 참여자 엔티티
 * DB_Schema.md portfolio_participants 테이블 기준
 * 수정 권한(can_edit) 및 소유자(is_owner) 관리
 *
 * @since 2026.05.16
 * @version 1.0.0
 * @author 신태훈, 조하겸
 */
@Entity
@Table(name = "portfolio_participants", uniqueConstraints = @UniqueConstraint(name = "uk_portfolio_user", columnNames = {
		"portfolio_id", "user_id" }))
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class PortfolioParticipant {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "portfolio_id", nullable = false)
	private Portfolio portfolio;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	/** 포트폴리오에서의 역할 (필수, 예: Backend, PM) */
	@Column(length = 100, nullable = false)
	private String role;

	/** 수정 권한 여부 */
	@Column(name = "can_edit")
	private boolean canEdit = true;

	/** 업로더/소유자 여부 */
	@Column(name = "is_owner")
	private boolean isOwner = false;

	@CreatedDate
	@Column(name = "joined_at", updatable = false)
	private LocalDateTime joinedAt;

	@Builder
	public PortfolioParticipant(Portfolio portfolio, User user, String role,
			boolean canEdit, boolean isOwner) {
		this.portfolio = portfolio;
		this.user = user;
		this.role = role;
		this.canEdit = canEdit;
		this.isOwner = isOwner;
	}
}
