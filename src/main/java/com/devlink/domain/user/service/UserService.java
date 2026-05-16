package com.devlink.domain.user.service;

import com.devlink.domain.user.dto.UserProfileResponse;
import com.devlink.domain.user.dto.UserUpdateRequest;
import com.devlink.domain.user.entity.User;
import com.devlink.domain.user.repository.UserRepository;
import com.devlink.global.exception.CustomException;
import com.devlink.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 서비스
 * 프로필 조회 및 수정
 *
 * @since 2026.05.16
 * @version 1.0.0
 * @author 신태훈, 조하겸
 */
@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	/**
	 * 내 프로필 조회
	 *
	 * @param userId 세션에서 가져온 사용자 ID
	 * @return 사용자 프로필 응답
	 */
	@Transactional(readOnly = true)
	public UserProfileResponse getMyProfile(Long userId) {
		User user = findUserById(userId);
		return UserProfileResponse.from(user);
	}

	/**
	 * 내 프로필 수정 (name, grade, githubLink만 수정 가능)
	 *
	 * @param userId  세션에서 가져온 사용자 ID
	 * @param request 수정 요청 DTO
	 * @return 수정된 사용자 프로필 응답
	 */
	@Transactional
	public UserProfileResponse updateMyProfile(Long userId, UserUpdateRequest request) {
		User user = findUserById(userId);
		user.updateProfile(request.getName(), request.getGrade(), request.getGithubLink());
		return UserProfileResponse.from(user);
	}

	/**
	 * ID로 사용자 조회 (내부 공통 메서드)
	 */
	private User findUserById(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
	}
}
