package com.devlink.domain.user.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 사용자 엔티티
 * DB_Schema.md users 테이블 기준
 * password 컬럼 제외 (포털 SSO 인증 방식)
 *
 * @since 2026.05.16
 * @version 1.0.0
 * @author 신태훈, 조하겸
 */
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/** 사용자 이름 (필수) */
	@Column(nullable = false, length = 50)
	private String name;

	/** 학교 포털 아이디 (필수, 중복 불가) */
	@Column(name = "portal_id", nullable = false, unique = true, length = 50)
	private String portalId;

	/** 학번 (중복 불가, 최초 등록 시 입력) */
	@Column(name = "student_id", unique = true, length = 20)
	private String studentId;

	/** 학년 (1~4학년) */
	@Column(nullable = false)
	private Integer grade;

	/** GitHub 프로필 링크 (선택) */
	@Column(name = "github_link", length = 300)
	private String githubLink;

	/** 사용자 유형 (가입 후 변경 불가) */
	@Enumerated(EnumType.STRING)
	@Column(name = "user_level", nullable = false, length = 20)
	private UserLevel userLevel;

	@CreatedDate
	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	@LastModifiedDate
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@Builder
	public User(String name, String portalId, String studentId, Integer grade,
			String githubLink, UserLevel userLevel) {
		this.name = name;
		this.portalId = portalId;
		this.studentId = studentId;
		this.grade = grade;
		this.githubLink = githubLink;
		this.userLevel = userLevel;
	}

	/**
	 * 프로필 수정 (name, grade, githubLink만 변경 가능)
	 * portalId, studentId, userLevel은 변경 불가
	 */
	public void updateProfile(String name, Integer grade, String githubLink) {
		this.name = name;
		this.grade = grade;
		this.githubLink = githubLink;
	}
}
