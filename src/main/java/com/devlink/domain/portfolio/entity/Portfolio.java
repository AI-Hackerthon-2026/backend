package com.devlink.domain.portfolio.entity;

import com.devlink.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 포트폴리오 엔티티
 * DB_Schema.md portfolios 테이블 기준
 * participants, role 컬럼 제거 → portfolio_participants 테이블로 관리
 *
 * @since 2026.05.16
 * @version 1.0.0
 * @author 신태훈, 조하겸
 */
@Entity
@Table(name = "portfolios")
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Portfolio {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/** 작성자 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	/** 프로젝트명 */
	@Column(name = "project_name", nullable = false, length = 100)
	private String projectName;

	/** 프로젝트 카테고리 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private PortfolioCategory category;

	/** 한 줄 요약 (최대 300자) */
	@Column(nullable = false, length = 300)
	private String summary;

	/** 상세 설명 */
	@Column(nullable = false, columnDefinition = "LONGTEXT")
	private String description;

	/** 대표 이미지 URL */
	@Column(name = "thumbnail_url", length = 500)
	private String thumbnailUrl;

	/** 실행화면/설계서 이미지 URL */
	@Column(name = "image_url", length = 500)
	private String imageUrl;

	/** GitHub 저장소 링크 (필수, 중복 불가) */
	@Column(name = "github_link", nullable = false, unique = true, length = 300)
	private String githubLink;

	/** 배포 링크 (선택) */
	@Column(name = "deployment_link", length = 300)
	private String deploymentLink;

	/** 프로젝트 시작일 */
	@Column(name = "start_date", nullable = false)
	private LocalDate startDate;

	/** 프로젝트 종료일 */
	@Column(name = "end_date", nullable = false)
	private LocalDate endDate;

	/** 공감 수 (캐시 필드) */
	@Column(name = "like_count")
	private int likeCount = 0;

	/** 소프트 삭제 여부 */
	@Column(name = "is_deleted")
	private boolean isDeleted = false;

	@CreatedDate
	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	@LastModifiedDate
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@Builder
	public Portfolio(User user, String projectName, PortfolioCategory category,
			String summary, String description, String thumbnailUrl,
			String imageUrl, String githubLink, String deploymentLink,
			LocalDate startDate, LocalDate endDate) {
		this.user = user;
		this.projectName = projectName;
		this.category = category;
		this.summary = summary;
		this.description = description;
		this.thumbnailUrl = thumbnailUrl;
		this.imageUrl = imageUrl;
		this.githubLink = githubLink;
		this.deploymentLink = deploymentLink;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	/** 포트폴리오 정보 수정 */
	public void update(String projectName, PortfolioCategory category, String summary,
			String description, String thumbnailUrl, String imageUrl,
			String githubLink, String deploymentLink,
			LocalDate startDate, LocalDate endDate) {
		this.projectName = projectName;
		this.category = category;
		this.summary = summary;
		this.description = description;
		this.thumbnailUrl = thumbnailUrl;
		this.imageUrl = imageUrl;
		this.githubLink = githubLink;
		this.deploymentLink = deploymentLink;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	/** 공감 수 증가 */
	public void increaseLikeCount() {
		this.likeCount++;
	}

	/** 공감 수 감소 */
	public void decreaseLikeCount() {
		if (this.likeCount > 0)
			this.likeCount--;
	}

	/** 소프트 삭제 처리 */
	public void softDelete() {
		this.isDeleted = true;
	}
}
