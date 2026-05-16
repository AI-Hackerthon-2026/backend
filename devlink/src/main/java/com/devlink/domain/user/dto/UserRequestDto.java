package com.devlink.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 요청 DTO
 *
 * @since : 2026.05.16
 * @version : 0.0.1
 * @author : DevLink Team
 */
@Getter
@NoArgsConstructor
public class UserRequestDto {

	/** 이름 */
	private String name;

	/** 학번 */
	private String studentId;

	/** 이메일 */
	private String email;

	/** 자기소개 */
	private String bio;

	/** 기술 스택 (쉼표 구분) */
	private String techStack;
}
