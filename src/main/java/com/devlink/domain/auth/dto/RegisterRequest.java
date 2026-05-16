package com.devlink.domain.auth.dto;

import com.devlink.domain.user.entity.UserLevel;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

/**
 * 최초 등록 요청 DTO
 * POST /api/auth/login 에서 isFirstLogin: true 반환 후 호출
 *
 * @since 2026.05.16
 * @version 1.0.0
 * @author 신태훈, 조하겸
 */
@Getter
public class RegisterRequest {

	@NotBlank(message = "학번은 필수 입력 항목입니다.")
	@Size(min = 9, max = 9, message = "학번은 9자리 숫자여야 합니다.")
	private String studentId;

	@NotBlank(message = "이름은 필수 입력 항목입니다.")
	private String name;

	@NotNull(message = "학년은 필수 입력 항목입니다.")
	@Min(value = 1, message = "학년은 1~4학년만 입력 가능합니다.")
	@Max(value = 4, message = "학년은 1~4학년만 입력 가능합니다.")
	private Integer grade;

	/** GitHub 링크 (선택) */
	private String githubLink;

	@NotNull(message = "사용자 유형은 필수 입력 항목입니다.")
	private UserLevel userLevel;
}
