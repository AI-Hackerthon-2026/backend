package com.devlink.domain.auth.dto;

import com.devlink.domain.user.entity.User;
import com.devlink.domain.user.entity.UserLevel;
import lombok.Builder;
import lombok.Getter;

/**
 * 로그인/등록 응답 DTO
 *
 * @since 2026.05.16
 * @version 1.0.0
 * @author 신태훈, 조하겸
 */
@Getter
@Builder
public class LoginResponse {

	private Long id;
	private String name;
	private String studentId;
	private UserLevel userLevel;

	/** 최초 로그인 여부 (true이면 register API 호출 필요) */
	private boolean isFirstLogin;

	/**
	 * 기존 회원 로그인 응답 생성
	 */
	public static LoginResponse ofExistingUser(User user) {
		return LoginResponse.builder()
			.id(user.getId())
			.name(user.getName())
			.studentId(user.getStudentId())
			.userLevel(user.getUserLevel())
			.isFirstLogin(false)
			.build();
	}

	/**
	 * 최초 로그인 응답 생성 (추가 정보 입력 필요)
	 */
	public static LoginResponse ofFirstLogin() {
		return LoginResponse.builder()
			.isFirstLogin(true)
			.build();
	}
}
