package com.devlink.domain.user.dto;

import com.devlink.domain.user.entity.User;
import com.devlink.domain.user.entity.UserLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 사용자 프로필 응답 DTO
 *
 * @since 2026.05.16
 * @version 1.0.0
 * @author 신태훈, 조하겸
 */
@Getter
@Builder
public class UserProfileResponse {

	private Long id;
	private String name;
	private String portalId;
	private String studentId;
	private Integer grade;
	private String githubLink;
	private UserLevel userLevel;
	private LocalDateTime createdAt;

	/**
	 * User 엔티티를 응답 DTO로 변환
	 */
	public static UserProfileResponse from(User user) {
		return UserProfileResponse.builder()
			.id(user.getId())
			.name(user.getName())
			.portalId(user.getPortalId())
			.studentId(user.getStudentId())
			.grade(user.getGrade())
			.githubLink(user.getGithubLink())
			.userLevel(user.getUserLevel())
			.createdAt(user.getCreatedAt())
			.build();
	}
}
