package com.devlink.domain.portfolio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

/**
 * 포트폴리오 참여자 요청 DTO
 *
 * @since 2026.05.16
 * @version 1.0.0
 * @author 신태훈, 조하겸
 */
@Getter
public class ParticipantRequest {

	@NotNull(message = "참여자 userId는 필수입니다.")
	private Long userId;

	/** 담당 역할 (필수) */
	@NotBlank(message = "참여자 역할은 필수 입력 항목입니다.")
	private String role;
}
