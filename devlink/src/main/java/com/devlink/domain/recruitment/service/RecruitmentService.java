package com.devlink.domain.recruitment.service;

import com.devlink.domain.recruitment.dto.RecruitmentRequestDto;
import com.devlink.domain.recruitment.dto.RecruitmentResponseDto;
import com.devlink.domain.recruitment.entity.Recruitment;
import com.devlink.domain.recruitment.repository.RecruitmentRepository;
import com.devlink.domain.user.entity.User;
import com.devlink.domain.user.repository.UserRepository;
import com.devlink.global.common.enums.RecruitmentStatus;
import com.devlink.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 프로젝트 팀원 모집 서비스
 *
 * @since : 2026.05.16
 * @version : 0.0.1
 * @author : DevLink Team
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecruitmentService {

	private final RecruitmentRepository recruitmentRepository;
	private final UserRepository userRepository;

	/**
	 * 모집 글 작성
	 *
	 * @param requestDto 모집 글 요청 정보
	 * @return 작성된 모집 글 응답 DTO
	 */
	@Transactional
	public RecruitmentResponseDto createRecruitment(RecruitmentRequestDto requestDto) {
		User user = userRepository.findById(requestDto.getUserId())
			.orElseThrow(() -> CustomException.NOT_FOUND);

		Recruitment recruitment = Recruitment.builder()
			.title(requestDto.getTitle())
			.description(requestDto.getDescription())
			.roles(requestDto.getRoles())
			.maxMembers(requestDto.getMaxMembers())
			.techStack(requestDto.getTechStack())
			.deadline(requestDto.getDeadline())
			.contactMethod(requestDto.getContactMethod())
			.user(user)
			.build();

		return RecruitmentResponseDto.from(recruitmentRepository.save(recruitment));
	}

	/**
	 * 모집 글 전체 목록 조회
	 *
	 * @return 모집 글 목록
	 */
	public List<RecruitmentResponseDto> getAllRecruitments() {
		return recruitmentRepository.findAll()
			.stream()
			.map(RecruitmentResponseDto::from)
			.collect(Collectors.toList());
	}

	/**
	 * 모집 글 단건 조회
	 *
	 * @param recruitmentId 모집 글 ID
	 * @return 모집 글 응답 DTO
	 */
	public RecruitmentResponseDto getRecruitmentById(Long recruitmentId) {
		Recruitment recruitment = recruitmentRepository.findById(recruitmentId)
			.orElseThrow(() -> CustomException.NOT_FOUND);

		return RecruitmentResponseDto.from(recruitment);
	}

	/**
	 * 모집 상태 변경
	 *
	 * @param recruitmentId 모집 글 ID
	 * @param status 변경할 상태
	 * @return 수정된 모집 글 응답 DTO
	 */
	@Transactional
	public RecruitmentResponseDto updateStatus(Long recruitmentId, RecruitmentStatus status) {
		Recruitment recruitment = recruitmentRepository.findById(recruitmentId)
			.orElseThrow(() -> CustomException.NOT_FOUND);

		recruitment.updateStatus(status);

		return RecruitmentResponseDto.from(recruitment);
	}
}
