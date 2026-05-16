package com.devlink.domain.user.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

/**
 * 사용자 프로필 수정 요청 DTO
 * portalId, studentId, userLevel은 수정 불가
 *
 * @since 2026.05.16
 * @version 1.0.0
 * @author 신태훈, 조하겸
 */
@Getter
public class UserUpdateRequest {

	@NotBlank(message = "이름은 필수 입력 항목입니다.")
	private String name;

	@NotNull(message = "학년은 필수 입력 항목입니다.")
	@Min(value = 1, message = "학년은 1~4학년만 입력 가능합니다.")
	@Max(value = 4, message = "학년은 1~4학년만 입력 가능합니다.")
	private Integer grade;

	/** GitHub 프로필 링크 (선택) */
	private String githubLink;
}
