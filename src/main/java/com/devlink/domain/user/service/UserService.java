package com.devlink.domain.user.service;

import com.devlink.domain.user.dto.UserProfileResponse;
import com.devlink.domain.user.dto.UserSearchResponse;
import com.devlink.domain.user.dto.UserUpdateRequest;
import com.devlink.domain.user.entity.User;
import com.devlink.domain.user.repository.UserRepository;
import com.devlink.global.common.ValidationUtils;
import com.devlink.global.exception.CustomException;
import com.devlink.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
		String githubLink = request.getGithubLink();
		if (githubLink != null && !githubLink.isBlank() && !ValidationUtils.isValidGitHubLink(githubLink)) {
			throw new CustomException(ErrorCode.INVALID_GITHUB_LINK_FORMAT);
		}
		User user = findUserById(userId);
		/* null인 필드는 기존 값 유지 (PATCH 방식) */
		String newName = request.getName() != null ? request.getName() : user.getName();
		Integer newGrade = request.getGrade() != null ? request.getGrade() : user.getGrade();
		String newGithubLink = (githubLink == null) ? user.getGithubLink()
			: (githubLink.isBlank() ? null : githubLink);
		user.updateProfile(newName, newGrade, newGithubLink);
		return UserProfileResponse.from(user);
	}

	/**
	 * 사용자 검색 (참여자 추가용)
	 * 이름 또는 학번으로 LIKE 검색, 본인은 결과에서 제외
	 *
	 * @param q           검색어 (이름 또는 학번 일부)
	 * @param requesterId 요청자 ID (결과에서 본인 제외)
	 * @return 사용자 검색 결과 목록
	 */
	@Transactional(readOnly = true)
	public List<UserSearchResponse> searchUsers(String keyword, Long requesterId) {
		if (keyword == null || keyword.isBlank()) {
			throw new CustomException(ErrorCode.INVALID_INPUT);
		}
		return userRepository.searchByNameOrStudentId(keyword.trim(), requesterId)
				.stream()
				.map(UserSearchResponse::from)
				.toList();
	}

	/**
	 * ID로 사용자 조회 (내부 공통 메서드)
	 */
	private User findUserById(Long userId) {
		return userRepository.findById(userId)
				.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
	}
}
