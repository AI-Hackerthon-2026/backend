package com.devlink.domain.recruitment.entity;

import com.devlink.domain.user.entity.User;
import com.devlink.global.common.BaseEntity;
import com.devlink.global.common.enums.RecruitmentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * 프로젝트 팀원 모집 엔티티
 *
 * @since : 2026.05.16
 * @version : 0.0.1
 * @author : DevLink Team
 */
@Entity
@Table(name = "recruitment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Recruitment extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/** 프로젝트 제목 */
	@Column(nullable = false, length = 100)
	private String title;

	/** 프로젝트 소개 */
	@Column(nullable = false, columnDefinition = "TEXT")
	private String description;

	/** 모집 역할 (쉼표 구분) */
	@Column(nullable = false, length = 200)
	private String roles;

	/** 모집 인원 */
	@Column(nullable = false)
	private int maxMembers;

	/** 필요 기술 스택 (쉼표 구분) */
	@Column(length = 300)
	private String techStack;

	/** 모집 마감일 */
	@Column(nullable = false)
	private LocalDate deadline;

	/** 모집 상태 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@Builder.Default
	private RecruitmentStatus status = RecruitmentStatus.OPEN;

	/** 연락/지원 방식 */
	@Column(length = 200)
	private String contactMethod;

	/** 작성자 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	/**
	 * 모집 상태 변경
	 *
	 * @param status 변경할 모집 상태
	 */
	public void updateStatus(RecruitmentStatus status) {
		this.status = status;
	}
}
