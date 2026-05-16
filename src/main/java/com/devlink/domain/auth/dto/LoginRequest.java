package com.devlink.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

/**
 * 포털 SSO 로그인 요청 DTO
 *
 * @since 2026.05.16
 * @version 1.0.0
 * @author 신태훈, 조하겸
 */
@Getter
public class LoginRequest {

	@NotBlank(message = "포털 아이디를 입력해주세요.")
	private String portalId;

	@NotBlank(message = "포털 비밀번호를 입력해주세요.")
	private String password;
}
