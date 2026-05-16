package com.devlink.domain.like.entity;

import com.devlink.domain.portfolio.entity.Portfolio;
import com.devlink.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 공감(좋아요) 엔티티
 * 한 사용자는 하나의 포트폴리오에 한 번만 공감 가능 (복합 유니크 제약)
 *
 * @since : 2026.05.16
 * @version : 0.0.1
 * @author : DevLink Team
 */
@Entity
@Table(
	name = "likes",
	uniqueConstraints = {
		@UniqueConstraint(columnNames = {"user_id", "portfolio_id"})
	}
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Like {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/** 공감한 사용자 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	/** 공감 대상 포트폴리오 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "portfolio_id", nullable = false)
	private Portfolio portfolio;

	/** 공감 일시 */
	@CreatedDate
	@Column(updatable = false)
	private LocalDateTime createdAt;
}
