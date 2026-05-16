package com.devlink.domain.portfolio.dto;

import com.devlink.domain.portfolio.entity.PortfolioCategory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

/**
 * 포트폴리오 작성 요청 DTO
 *
 * @since 2026.05.16
 * @version 1.0.0
 * @author 신태훈, 조하겸
 */
@Getter
public class PortfolioCreateRequest {

	@NotBlank(message = "프로젝트명을 입력해주세요.")
	private String projectName;

	@NotNull(message = "카테고리를 선택해주세요.")
	private PortfolioCategory category;

	@NotBlank(message = "한 줄 요약을 입력해주세요.")
	private String summary;

	@NotBlank(message = "상세 설명을 입력해주세요.")
	private String description;

	private String thumbnailUrl;

	@NotBlank(message = "본인의 담당 역할을 입력해주세요.")
	private String myRole;

	private String githubLink;

	private String deploymentLink;

	@NotEmpty(message = "기술스택을 1개 이상 입력해주세요.")
	private List<String> skills;

	/** 참여자 목록 (GRADUATION/P_PROJECT 카테고리는 필수) */
	@Valid
	private List<ParticipantRequest> participants;

	@NotNull(message = "시작일을 입력해주세요.")
	private LocalDate startDate;

	@NotNull(message = "종료일을 입력해주세요.")
	private LocalDate endDate;
}
