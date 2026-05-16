package com.devlink.domain.user.dto;

import com.devlink.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 사용자 응답 DTO
 *
 * @return : 사용자 정보
 * @since : 2026.05.16
 * @version : 0.0.1
 * @author : DevLink Team
 */
@Getter
@Builder
public class UserResponseDto {

	private Long id;
	private String name;
	private String studentId;
	private String email;
	private String bio;
	private String techStack;
	private LocalDateTime createdAt;

	/**
	 * User 엔티티 → UserResponseDto 변환
	 *
	 * @param user User 엔티티
	 * @return UserResponseDto
	 */
	public static UserResponseDto from(User user) {
		return UserResponseDto.builder()
			.id(user.getId())
			.name(user.getName())
			.studentId(user.getStudentId())
			.email(user.getEmail())
			.bio(user.getBio())
			.techStack(user.getTechStack())
			.createdAt(user.getCreatedAt())
			.build();
	}
}
