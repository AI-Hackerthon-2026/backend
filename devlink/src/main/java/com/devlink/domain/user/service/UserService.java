package com.devlink.domain.user.service;

import com.devlink.domain.user.dto.UserRequestDto;
import com.devlink.domain.user.dto.UserResponseDto;
import com.devlink.domain.user.entity.User;
import com.devlink.domain.user.repository.UserRepository;
import com.devlink.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 서비스
 *
 * @since : 2026.05.16
 * @version : 0.0.1
 * @author : DevLink Team
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

	private final UserRepository userRepository;

	/**
	 * 사용자 등록
	 *
	 * @param requestDto 사용자 요청 정보
	 * @return 등록된 사용자 응답 DTO
	 * @throws CustomException 이메일 중복 시 예외 발생
	 */
	@Transactional
	public UserResponseDto registerUser(UserRequestDto requestDto) {
		if (userRepository.existsByEmail(requestDto.getEmail())) {
			throw new CustomException(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다.");
		}

		User user = User.builder()
			.name(requestDto.getName())
			.studentId(requestDto.getStudentId())
			.email(requestDto.getEmail())
			.bio(requestDto.getBio())
			.techStack(requestDto.getTechStack())
			.build();

		return UserResponseDto.from(userRepository.save(user));
	}

	/**
	 * 사용자 단건 조회
	 *
	 * @param userId 사용자 ID
	 * @return 사용자 응답 DTO
	 */
	public UserResponseDto getUserById(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> CustomException.NOT_FOUND);

		return UserResponseDto.from(user);
	}
}
