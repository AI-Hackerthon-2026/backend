package com.devlink.domain.user.entity;

import com.devlink.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 사용자 엔티티
 *
 * @since : 2026.05.16
 * @version : 0.0.1
 * @author : DevLink Team
 */
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/** 이름 */
	@Column(nullable = false, length = 50)
	private String name;

	/** 학번 */
	@Column(nullable = false, unique = true, length = 20)
	private String studentId;

	/** 이메일 */
	@Column(nullable = false, unique = true, length = 100)
	private String email;

	/** 자기소개 */
	@Column(length = 500)
	private String bio;

	/** 기술 스택 (쉼표 구분 문자열) */
	@Column(length = 300)
	private String techStack;
}
