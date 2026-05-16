package com.devlink.domain.portfolio.entity;

import com.devlink.domain.user.entity.User;
import com.devlink.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * 포트폴리오 엔티티
 *
 * @since : 2026.05.16
 * @version : 0.0.1
 * @author : DevLink Team
 */
@Entity
@Table(name = "portfolio")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Portfolio extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/** 프로젝트명 */
	@Column(nullable = false, length = 100)
	private String title;

	/** 프로젝트 요약 */
	@Column(nullable = false, length = 300)
	private String summary;

	/** 프로젝트 상세 설명 */
	@Column(nullable = false, columnDefinition = "TEXT")
	private String description;

	/** 대표 이미지 URL */
	@Column(length = 500)
	private String thumbnailUrl;

	/** GitHub 링크 */
	@Column(length = 300)
	private String githubUrl;

	/** 배포 링크 */
	@Column(length = 300)
	private String deployUrl;

	/** 사용 기술 스택 (쉼표 구분) */
	@Column(length = 300)
	private String techStack;

	/** 참여 인원 */
	private int teamSize;

	/** 담당 역할 */
	@Column(length = 200)
	private String myRole;

	/** 개발 시작일 */
	private LocalDate startDate;

	/** 개발 종료일 */
	private LocalDate endDate;

	/** 공감 수 */
	@Column(nullable = false)
	@Builder.Default
	private int likeCount = 0;

	/** 작성자 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	/**
	 * 공감 수 증가
	 */
	public void increaseLikeCount() {
		this.likeCount++;
	}

	/**
	 * 공감 수 감소
	 */
	public void decreaseLikeCount() {
		if (this.likeCount > 0) {
			this.likeCount--;
		}
	}
}
