package com.devlink.domain.skill.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 기술스택 엔티티
 * 사용자 자유 입력 → 자동 등록 구조
 *
 * @since 2026.05.16
 * @version 1.0.0
 * @author DevLink Team
 */
@Entity
@Table(name = "skills")
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Skill {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/** 기술명 (중복 불가) */
	@Column(nullable = false, unique = true, length = 50)
	private String name;

	/** 기술 분류 (Language, Frontend, Backend, Database, DevOps, Mobile, Other) */
	@Column(length = 30)
	private String category;

	@CreatedDate
	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	@Builder
	public Skill(String name, String category) {
		this.name = name;
		this.category = category;
	}
}
