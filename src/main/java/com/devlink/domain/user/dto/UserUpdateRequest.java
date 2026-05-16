package com.devlink.domain.user.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;

/**
 * 사용자 프로필 수정 요청 DTO
 * portalId, studentId, userLevel은 수정 불가
 * PATCH 방식: null인 필드는 기존 값 유지
 *
 * @since 2026.05.16
 * @version 1.0.0
 * @author 신태훈, 조하겸
 */
@Getter
public class UserUpdateRequest {

	/** 이름 (null이면 기존 값 유지) */
	private String name;

	/** 학년 (null이면 기존 값 유지, 제공 시 1~4 범위 검증) */
	@Min(value = 1, message = "학년은 1~4학년만 입력 가능합니다.")
	@Max(value = 4, message = "학년은 1~4학년만 입력 가능합니다.")
	private Integer grade;

	/** GitHub 프로필 링크 (null이면 기존 값 유지, 빈 문자열이면 삭제) */
	private String githubLink;
}
