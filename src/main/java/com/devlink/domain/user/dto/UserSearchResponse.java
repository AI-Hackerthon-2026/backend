package com.devlink.domain.user.dto;

import com.devlink.domain.user.entity.User;
import com.devlink.domain.user.entity.UserLevel;
import lombok.Builder;
import lombok.Getter;

/**
 * 사용자 검색 응답 DTO
 * GET /api/users/search — 참여자 추가용 검색 결과
 *
 * @since 2026.05.17
 * @version 1.0.0
 * @author 신태훈, 조하겸
 */
@Getter
@Builder
public class UserSearchResponse {

	private Long id;
	private String name;
	private String studentId;
	private UserLevel userLevel;

	public static UserSearchResponse from(User user) {
		return UserSearchResponse.builder()
			.id(user.getId())
			.name(user.getName())
			.studentId(user.getStudentId())
			.userLevel(user.getUserLevel())
			.build();
	}
}
