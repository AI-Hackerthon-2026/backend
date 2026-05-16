package com.devlink.domain.portfolio.service;

import com.devlink.domain.like.repository.LikeRepository;
import com.devlink.domain.portfolio.dto.*;
import com.devlink.domain.portfolio.entity.Portfolio;
import com.devlink.domain.portfolio.entity.PortfolioCategory;
import com.devlink.domain.portfolio.entity.PortfolioParticipant;
import com.devlink.domain.portfolio.repository.PortfolioParticipantRepository;
import com.devlink.domain.portfolio.repository.PortfolioRepository;
import com.devlink.domain.skill.entity.PortfolioSkill;
import com.devlink.domain.skill.entity.Skill;
import com.devlink.domain.skill.repository.PortfolioSkillRepository;
import com.devlink.domain.skill.repository.SkillRepository;
import com.devlink.domain.user.entity.User;
import com.devlink.domain.user.repository.UserRepository;
import com.devlink.global.exception.CustomException;
import com.devlink.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 포트폴리오 서비스
 * 포트폴리오 CRUD + 기술스택 자동 등록 + 참여자 관리
 *
 * @since 2026.05.16
 * @version 1.0.0
 * @author 신태훈, 조하겸
 */
@Service
@RequiredArgsConstructor
public class PortfolioService {

	private final PortfolioRepository portfolioRepository;
	private final PortfolioParticipantRepository participantRepository;
	private final PortfolioSkillRepository portfolioSkillRepository;
	private final SkillRepository skillRepository;
	private final UserRepository userRepository;
	private final LikeRepository likeRepository;

	/**
	 * 포트폴리오 전체 목록 조회
	 * category, skills 파라미터로 필터, sort로 정렬
	 *
	 * @param category 커테고리 필터 (null이면 전체)
	 * @param skills 필터링할 기술 목록 (null이면 전체)
	 * @param sort 정렬 기준 (LATEST, LIKES)
	 */
	@Transactional(readOnly = true)
	public List<PortfolioListResponse> getPortfolioList(String category, List<String> skills, String sort) {
		List<Portfolio> portfolios;

		if (skills == null || skills.isEmpty()) {
			/* 기술필터 없음: 정렬 기준으로 전체 조회 */
			portfolios = "LIKES".equalsIgnoreCase(sort)
				? portfolioRepository.findAllByIsDeletedFalseOrderByLikeCountDescCreatedAtDesc()
				: portfolioRepository.findAllByIsDeletedFalseOrderByCreatedAtDesc();
		} else {
			/* 기술스택 필터 적용 */
			portfolios = "LIKES".equalsIgnoreCase(sort)
				? portfolioRepository.findBySkillsAndLikes(skills)
				: portfolioRepository.findBySkillsAndLatest(skills);
		}

		/* 커테고리 필터 적용 */
		if (category != null && !category.isBlank()) {
			try {
				PortfolioCategory cat = PortfolioCategory.valueOf(category.toUpperCase());
				portfolios = portfolios.stream()
					.filter(p -> p.getCategory() == cat)
					.toList();
			} catch (IllegalArgumentException ignored) {
				/* 잘못된 커테고리 값이면 필터 무시 */
			}
		}

		return portfolios.stream()
			.map(p -> {
				List<PortfolioSkill> ps = portfolioSkillRepository.findAllByPortfolio(p);
				List<PortfolioParticipant> parts = participantRepository.findAllByPortfolio(p);
				return PortfolioListResponse.from(p, ps, parts);
			})
			.toList();
	}

	/**
	 * 포트폴리오 상세 조회
	 *
	 * @param portfolioId 포트폴리오 ID
	 * @param userId      현재 로그인 사용자 ID (비로그인이면 null)
	 */
	@Transactional(readOnly = true)
	public PortfolioDetailResponse getPortfolioDetail(Long portfolioId, Long userId) {
		Portfolio portfolio = findActivePortfolio(portfolioId);
		List<PortfolioSkill> skills = portfolioSkillRepository.findAllByPortfolio(portfolio);
		List<PortfolioParticipant> participants = participantRepository.findAllByPortfolio(portfolio);

		boolean canEdit = false;
		boolean isOwner = false;
		boolean isLiked = false;

		if (userId != null) {
			User user = userRepository.findById(userId).orElse(null);
			if (user != null) {
				canEdit = participantRepository.existsByPortfolioAndUserAndCanEditTrue(portfolio, user);
				isOwner = participantRepository.existsByPortfolioAndUserAndIsOwnerTrue(portfolio, user);
				isLiked = likeRepository.existsByUserAndPortfolio(user, portfolio);
			}
		}

		return PortfolioDetailResponse.from(portfolio, skills, participants, canEdit, isOwner, isLiked);
	}

	/**
	 * 인기 포트폴리오 TOP 3 (메인 대시보드용, 전체 누적 공감 상위 3개)
	 */
	@Transactional(readOnly = true)
	public List<PortfolioListResponse> getPopular() {
		return portfolioRepository.findTop3ByIsDeletedFalseOrderByLikeCountDescCreatedAtDesc()
			.stream()
			.map(p -> {
				List<PortfolioSkill> ps = portfolioSkillRepository.findAllByPortfolio(p);
				List<PortfolioParticipant> parts = participantRepository.findAllByPortfolio(p);
				return PortfolioListResponse.from(p, ps, parts);
			})
			.toList();
	}

	/**
	 * 랭킹 목록 조회 (공감 많은 순, period 파라미터로 이번/지난 학기 필터)
	 * 현재는 전체 기간 기준으로만 동작 (semester 구분은 추후 연동일에 확장)
	 *
	 * @param period 기간 필터 (ALL_TIME / CURRENT_SEMESTER / LAST_SEMESTER, 현재 ALL_TIME로 동작)
	 */
	@Transactional(readOnly = true)
	public List<PortfolioListResponse> getRanking(String period) {
		return getPortfolioList(null, null, "LIKES");
	}

	/**
	 * 포트폴리오 작성
	 * 기술스택 자동 등록 + 참여자 등록 (is_owner=true 작성자)
	 *
	 * @param request  작성 요청 DTO
	 * @param authorId 작성자 사용자 ID
	 */
	@Transactional
	public PortfolioDetailResponse createPortfolio(PortfolioCreateRequest request, Long authorId) {
		User author = userRepository.findById(authorId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		/* 날짜 유효성 검사 */
		if (request.getEndDate().isBefore(request.getStartDate())) {
			throw new CustomException(ErrorCode.INVALID_DATE_RANGE);
		}

		/* GitHub 링크 중복 확인 */
		if (portfolioRepository.existsByGithubLinkAndIsDeletedFalse(request.getGithubLink())) {
			throw new CustomException(ErrorCode.DUPLICATE_GITHUB_LINK);
		}

		/* GRADUATION, P_PROJECT는 참여자 필수 */
		if ((request.getCategory() == PortfolioCategory.GRADUATION
			|| request.getCategory() == PortfolioCategory.P_PROJECT)
			&& (request.getParticipants() == null || request.getParticipants().isEmpty())) {
			throw new CustomException(ErrorCode.PARTICIPANT_REQUIRED);
		}

		/* 포트폴리오 저장 */
		Portfolio portfolio = Portfolio.builder()
			.user(author)
			.projectName(request.getProjectName())
			.category(request.getCategory())
			.summary(request.getSummary())
			.description(request.getDescription())
			.thumbnailUrl(request.getThumbnailUrl())
			.githubLink(request.getGithubLink())
			.deploymentLink(request.getDeploymentLink())
			.startDate(request.getStartDate())
			.endDate(request.getEndDate())
			.build();

		portfolioRepository.save(portfolio);

		/* 작성자를 소유자로 참여자 등록 (myRole 사용) */
		participantRepository.save(PortfolioParticipant.builder()
			.portfolio(portfolio)
			.user(author)
			.role(request.getMyRole())
			.canEdit(true)
			.isOwner(true)
			.build());

		/* 추가 참여자 등록 */
		if (request.getParticipants() != null) {
			for (ParticipantRequest p : request.getParticipants()) {
				User participant = userRepository.findById(p.getUserId())
					.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
				participantRepository.save(PortfolioParticipant.builder()
					.portfolio(portfolio)
					.user(participant)
					.role(p.getRole())
					.canEdit(true)
					.isOwner(false)
					.build());
			}
		}

		/* 기술스택 등록 (없으면 자동 생성) */
		saveSkills(portfolio, request.getSkills());

		return getPortfolioDetail(portfolio.getId(), authorId);
	}

	/**
	 * 포트폴리오 수정 (can_edit=true인 참여자만 가능)
	 *
	 * @param portfolioId 포트폴리오 ID
	 * @param request     수정 요청 DTO
	 * @param userId      현재 로그인 사용자 ID
	 */
	@Transactional
	public PortfolioDetailResponse updatePortfolio(Long portfolioId, PortfolioUpdateRequest request, Long userId) {
		Portfolio portfolio = findActivePortfolio(portfolioId);
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		/* 수정 권한 확인 */
		if (!participantRepository.existsByPortfolioAndUserAndCanEditTrue(portfolio, user)) {
			throw new CustomException(ErrorCode.PORTFOLIO_ACCESS_DENIED);
		}

		/* 날짜 유효성 검사 */
		if (request.getEndDate().isBefore(request.getStartDate())) {
			throw new CustomException(ErrorCode.INVALID_DATE_RANGE);
		}

		/* GitHub 링크 중복 확인 (자기 자신 제외) */
		if (portfolioRepository.existsByGithubLinkAndIsDeletedFalseAndIdNot(
			request.getGithubLink(), portfolioId)) {
			throw new CustomException(ErrorCode.DUPLICATE_GITHUB_LINK);
		}

		/* GRADUATION, P_PROJECT는 참여자 필수 */
		if ((request.getCategory() == PortfolioCategory.GRADUATION
			|| request.getCategory() == PortfolioCategory.P_PROJECT)
			&& (request.getParticipants() == null || request.getParticipants().isEmpty())) {
			throw new CustomException(ErrorCode.PARTICIPANT_REQUIRED);
		}

		/* 포트폴리오 정보 수정 */
		portfolio.update(request.getProjectName(), request.getCategory(),
			request.getSummary(), request.getDescription(),
			request.getThumbnailUrl(),
			request.getGithubLink(), request.getDeploymentLink(),
			request.getStartDate(), request.getEndDate());

		/* 기술스택 재등록 */
		portfolioSkillRepository.deleteAllByPortfolio(portfolio);
		saveSkills(portfolio, request.getSkills());

		/* 참여자 재등록 (소유자 제외하고 삭제 후 재등록) */
		if (request.getParticipants() != null) {
			PortfolioParticipant ownerEntry = participantRepository.findAllByPortfolio(portfolio)
				.stream().filter(PortfolioParticipant::isOwner).findFirst().orElse(null);
			participantRepository.deleteAllByPortfolio(portfolio);
			if (ownerEntry != null) {
				participantRepository.save(PortfolioParticipant.builder()
					.portfolio(portfolio)
					.user(ownerEntry.getUser())
					.role(ownerEntry.getRole())
					.canEdit(true)
					.isOwner(true)
					.build());
			}
			for (ParticipantRequest p : request.getParticipants()) {
				User participant = userRepository.findById(p.getUserId())
					.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
				participantRepository.save(PortfolioParticipant.builder()
					.portfolio(portfolio)
					.user(participant)
					.role(p.getRole())
					.canEdit(true)
					.isOwner(false)
					.build());
			}
		}

		return getPortfolioDetail(portfolioId, userId);
	}

	/**
	 * 포트폴리오 소프트 삭제 (is_owner=true인 작성자만 가능)
	 *
	 * @param portfolioId 포트폴리오 ID
	 * @param userId      현재 로그인 사용자 ID
	 */
	@Transactional
	public void deletePortfolio(Long portfolioId, Long userId) {
		Portfolio portfolio = findActivePortfolio(portfolioId);
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		/* 소유자 권한 확인 */
		if (!participantRepository.existsByPortfolioAndUserAndIsOwnerTrue(portfolio, user)) {
			throw new CustomException(ErrorCode.PORTFOLIO_DELETE_DENIED);
		}

		portfolio.softDelete();
	}

	/**
	 * 내 포트폴리오 목록 조회 (참여자 포함)
	 *
	 * @param userId 현재 로그인 사용자 ID
	 */
	@Transactional(readOnly = true)
	public List<PortfolioListResponse> getMyPortfolios(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		return participantRepository.findAllByUser(user).stream()
			.map(PortfolioParticipant::getPortfolio)
			.filter(p -> !p.isDeleted())
			.map(p -> {
				List<PortfolioSkill> ps = portfolioSkillRepository.findAllByPortfolio(p);
				List<PortfolioParticipant> parts = participantRepository.findAllByPortfolio(p);
				return PortfolioListResponse.from(p, ps, parts);
			})
			.toList();
	}

	/**
	 * 기술스택 저장 (없으면 자동 생성 후 매핑)
	 */
	private void saveSkills(Portfolio portfolio, List<String> skillNames) {
		if (skillNames == null) return;
		for (String skillName : skillNames) {
			Skill skill = skillRepository.findByName(skillName)
				.orElseGet(() -> skillRepository.save(Skill.builder()
					.name(skillName)
					.category("Other")
					.build()));
			portfolioSkillRepository.save(PortfolioSkill.builder()
				.portfolio(portfolio)
				.skill(skill)
				.build());
		}
	}

	/**
	 * 삭제되지 않은 포트폴리오 조회 내부 메서드
	 */
	private Portfolio findActivePortfolio(Long portfolioId) {
		return portfolioRepository.findByIdAndIsDeletedFalse(portfolioId)
			.orElseThrow(() -> new CustomException(ErrorCode.PORTFOLIO_NOT_FOUND));
	}
}
