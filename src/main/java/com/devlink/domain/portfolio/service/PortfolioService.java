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
import com.devlink.global.common.PageResponse;
import com.devlink.global.exception.CustomException;
import com.devlink.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
	 * @param category 카테고리 필터 (null이면 전체)
	 * @param skills   쉼표 구분 기술 목록 (null이면 전체)
	 * @param sort     정렬 기준 (LATEST, LIKES)
	 * @param page     페이지 번호 (0부터 시작)
	 * @param size     페이지 크기
	 * @param userId   현재 로그인 사용자 ID
	 */
	@Transactional(readOnly = true)
	public PageResponse<PortfolioListResponse> getPortfolioList(
			String category,
			String skills,
			String sort,
			int page,
			int size,
			Long userId) {
		PortfolioCategory portfolioCategory = parseCategory(category);
		List<String> skillNames = parseSkills(skills);
		Sort sortOrder = "LIKES".equalsIgnoreCase(sort)
				? Sort.by(Sort.Direction.DESC, "likeCount", "createdAt")
				: Sort.by(Sort.Direction.DESC, "createdAt");
		Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), sortOrder);

		Page<Portfolio> portfolioPage;
		if (skillNames.isEmpty()) {
			if (portfolioCategory == null) {
				portfolioPage = "LIKES".equalsIgnoreCase(sort)
						? portfolioRepository.findAllByIsDeletedFalseOrderByLikeCountDescCreatedAtDesc(pageable)
						: portfolioRepository.findAllByIsDeletedFalseOrderByCreatedAtDesc(pageable);
			} else {
				portfolioPage = "LIKES".equalsIgnoreCase(sort)
						? portfolioRepository.findAllByCategoryAndIsDeletedFalseOrderByLikeCountDescCreatedAtDesc(
								portfolioCategory, pageable)
						: portfolioRepository.findAllByCategoryAndIsDeletedFalseOrderByCreatedAtDesc(
								portfolioCategory, pageable);
			}
		} else {
			portfolioPage = "LIKES".equalsIgnoreCase(sort)
					? portfolioRepository.findBySkillsAndLikes(portfolioCategory, skillNames, pageable)
					: portfolioRepository.findBySkillsAndLatest(portfolioCategory, skillNames, pageable);
		}

		User currentUser = userId != null
				? userRepository.findById(userId).orElse(null)
				: null;

		Page<PortfolioListResponse> responsePage = portfolioPage.map(portfolio -> {
			List<PortfolioSkill> ps = portfolioSkillRepository.findAllByPortfolio(portfolio);
			List<PortfolioParticipant> parts = participantRepository.findAllByPortfolio(portfolio);
			boolean isLiked = currentUser != null && likeRepository.existsByUserAndPortfolio(currentUser, portfolio);
			return PortfolioListResponse.from(portfolio, ps, parts, isLiked);
		});

		return PageResponse.of(responsePage);
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
	 * 메인 배너 TOP 3 조회
	 */
	@Transactional(readOnly = true)
	public List<PortfolioTopResponse> getTopPortfolios() {
		LocalDateTime now = LocalDateTime.now();
		SemesterRange currentSemester = getCurrentSemesterRange(now);
		SemesterRange lastSemester = getLastSemesterRange(now);

		List<PortfolioTopResponse> topResponses = new ArrayList<>();

		portfolioRepository
				.findTopByIsDeletedFalseAndCreatedAtBetweenOrderByLikeCountDescCreatedAtDesc(
						currentSemester.startAt(), currentSemester.endAt())
				.ifPresent(portfolio -> topResponses.add(
						PortfolioTopResponse.from(portfolio,
								portfolioSkillRepository.findAllByPortfolio(portfolio),
								"CURRENT_SEMESTER", "이번 학기")));

		portfolioRepository
				.findTopByIsDeletedFalseAndCreatedAtBetweenOrderByLikeCountDescCreatedAtDesc(
						lastSemester.startAt(), lastSemester.endAt())
				.ifPresent(portfolio -> topResponses.add(
						PortfolioTopResponse.from(portfolio,
								portfolioSkillRepository.findAllByPortfolio(portfolio),
								"LAST_SEMESTER", "지난 학기")));

		portfolioRepository
				.findTopByIsDeletedFalseOrderByLikeCountDescCreatedAtDesc()
				.ifPresent(portfolio -> topResponses.add(
						PortfolioTopResponse.from(portfolio,
								portfolioSkillRepository.findAllByPortfolio(portfolio),
								"ALL_TIME", "전체 기간")));

		return topResponses;
	}

	/**
	 * 랭킹 목록 조회 (공감 많은 순, period 파라미터로 이번/지난 학기 필터)
	 *
	 * @param period 기간 필터 (ALL_TIME / CURRENT_SEMESTER / LAST_SEMESTER)
	 * @param userId 현재 로그인 사용자 ID (비로그인이면 null)
	 */
	@Transactional(readOnly = true)
	public List<PortfolioListResponse> getRanking(String period, Long userId) {
		List<Portfolio> portfolios;
		LocalDateTime now = LocalDateTime.now();

		if ("CURRENT_SEMESTER".equalsIgnoreCase(period)) {
			SemesterRange currentSemester = getCurrentSemesterRange(now);
			portfolios = portfolioRepository
					.findAllByIsDeletedFalseAndCreatedAtBetweenOrderByLikeCountDescCreatedAtDesc(
							currentSemester.startAt(), currentSemester.endAt());
		} else if ("LAST_SEMESTER".equalsIgnoreCase(period)) {
			SemesterRange lastSemester = getLastSemesterRange(now);
			portfolios = portfolioRepository
					.findAllByIsDeletedFalseAndCreatedAtBetweenOrderByLikeCountDescCreatedAtDesc(
							lastSemester.startAt(), lastSemester.endAt());
		} else {
			portfolios = portfolioRepository.findAllByIsDeletedFalseOrderByLikeCountDescCreatedAtDesc();
		}

		User currentUser = null;
		if (userId != null) {
			currentUser = userRepository.findById(userId).orElse(null);
		}

		User finalUser = currentUser;
		return portfolios.stream()
				.map(portfolio -> {
					List<PortfolioSkill> skills = portfolioSkillRepository.findAllByPortfolio(portfolio);
					List<PortfolioParticipant> participants = participantRepository.findAllByPortfolio(portfolio);
					boolean isLiked = finalUser != null
							&& likeRepository.existsByUserAndPortfolio(finalUser, portfolio);
					return PortfolioListResponse.from(portfolio, skills, participants, isLiked);
				})
				.toList();
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

		/* GitHub 링크 중복 확인 (null이면 생략) */
		if (request.getGithubLink() != null
				&& portfolioRepository.existsByGithubLinkAndIsDeletedFalse(request.getGithubLink())) {
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

		/* 작성자를 소유자로 참여자 등록 (myRole 사용) + 추가 참여자 일괄 저장 */
		List<PortfolioParticipant> participants = new ArrayList<>();
		participants.add(PortfolioParticipant.builder()
				.portfolio(portfolio)
				.user(author)
				.role(request.getMyRole())
				.canEdit(true)
				.isOwner(true)
				.build());

		if (request.getParticipants() != null) {
			for (ParticipantRequest p : request.getParticipants()) {
				/* 작성자가 참여자 목록에 포함된 경우 중복 등록 방지 */
				if (authorId.equals(p.getUserId())) {
					continue;
				}
				User participant = userRepository.findById(p.getUserId())
						.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
				participants.add(PortfolioParticipant.builder()
						.portfolio(portfolio)
						.user(participant)
						.role(p.getRole())
						.canEdit(true)
						.isOwner(false)
						.build());
			}
		}
		participantRepository.saveAll(participants);

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

		/* GitHub 링크 중복 확인 (null이면 생략, 자기 자신 제외) */
		if (request.getGithubLink() != null
				&& portfolioRepository.existsByGithubLinkAndIsDeletedFalseAndIdNot(
						request.getGithubLink(), portfolioId)) {
			throw new CustomException(ErrorCode.DUPLICATE_GITHUB_LINK);
		}

		/* GRADUATION, P_PROJECT는 참여자 명시 시 빈 목록 불가 (null이면 기존 유지이므로 허용) */
		if ((request.getCategory() == PortfolioCategory.GRADUATION
				|| request.getCategory() == PortfolioCategory.P_PROJECT)
				&& request.getParticipants() != null && request.getParticipants().isEmpty()) {
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

		/* 참여자 처리: participants가 있으면 재등록, null이면 소유자 역할만 myRole로 갱신 */
		List<PortfolioParticipant> currentParticipants = participantRepository.findAllByPortfolio(portfolio);
		PortfolioParticipant ownerEntry = currentParticipants.stream()
				.filter(PortfolioParticipant::isOwner).findFirst().orElse(null);
		Long ownerId = ownerEntry != null ? ownerEntry.getUser().getId() : null;

		if (request.getParticipants() != null) {
			/* 참여자 목록 명시 → 전체 재등록 */
			participantRepository.deleteAllByPortfolio(portfolio);
			List<PortfolioParticipant> toSave = new ArrayList<>();
			if (ownerEntry != null) {
				String ownerRole = (request.getMyRole() != null && !request.getMyRole().isBlank())
						? request.getMyRole() : ownerEntry.getRole();
				toSave.add(PortfolioParticipant.builder()
						.portfolio(portfolio)
						.user(ownerEntry.getUser())
						.role(ownerRole)
						.canEdit(true)
						.isOwner(true)
						.build());
			}
			for (ParticipantRequest p : request.getParticipants()) {
				if (ownerId != null && ownerId.equals(p.getUserId())) {
					continue;
				}
				User participant = userRepository.findById(p.getUserId())
						.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
				toSave.add(PortfolioParticipant.builder()
						.portfolio(portfolio)
						.user(participant)
						.role(p.getRole())
						.canEdit(true)
						.isOwner(false)
						.build());
			}
			participantRepository.saveAll(toSave);
		} else if (request.getMyRole() != null && !request.getMyRole().isBlank() && ownerEntry != null) {
			/* 참여자 목록 없이 myRole만 제공 → 소유자 역할만 갱신 (삭제 후 재등록) */
			participantRepository.deleteAllByPortfolio(portfolio);
			List<PortfolioParticipant> toSave = new ArrayList<>();
			toSave.add(PortfolioParticipant.builder()
					.portfolio(portfolio)
					.user(ownerEntry.getUser())
					.role(request.getMyRole())
					.canEdit(true)
					.isOwner(true)
					.build());
			currentParticipants.stream()
					.filter(p -> !p.isOwner())
					.forEach(p -> toSave.add(PortfolioParticipant.builder()
							.portfolio(portfolio)
							.user(p.getUser())
							.role(p.getRole())
							.canEdit(p.isCanEdit())
							.isOwner(false)
							.build()));
			participantRepository.saveAll(toSave);
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
				.sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
				.map(p -> {
					List<PortfolioSkill> ps = portfolioSkillRepository.findAllByPortfolio(p);
					List<PortfolioParticipant> parts = participantRepository.findAllByPortfolio(p);
					return PortfolioListResponse.from(p, ps, parts);
				})
				.toList();
	}

	/**
	 * 기술스택 저장 (없으면 자동 생성 후 매핑, saveAll로 일괄 저장)
	 */
	private void saveSkills(Portfolio portfolio, List<String> skillNames) {
		if (skillNames == null)
			return;
		List<PortfolioSkill> toSave = new ArrayList<>();
		for (String skillName : skillNames) {
			Skill skill = skillRepository.findByName(skillName)
					.orElseGet(() -> skillRepository.save(Skill.builder()
							.name(skillName)
							.category("Other")
							.build()));
			toSave.add(PortfolioSkill.builder()
					.portfolio(portfolio)
					.skill(skill)
					.build());
		}
		portfolioSkillRepository.saveAll(toSave);
	}

	private PortfolioCategory parseCategory(String category) {
		if (category == null || category.isBlank()) {
			return null;
		}
		try {
			return PortfolioCategory.valueOf(category.toUpperCase());
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	private List<String> parseSkills(String skills) {
		if (skills == null || skills.isBlank()) {
			return List.of();
		}
		return Arrays.stream(skills.split(","))
				.map(String::trim)
				.filter(value -> !value.isEmpty())
				.toList();
	}

	private SemesterRange getCurrentSemesterRange(LocalDateTime now) {
		Month month = now.getMonth();
		int year = now.getYear();
		if (month.getValue() >= Month.MARCH.getValue() && month.getValue() <= Month.AUGUST.getValue()) {
			return new SemesterRange(
					LocalDate.of(year, Month.MARCH, 1).atStartOfDay(),
					LocalDate.of(year, Month.AUGUST, 31).atTime(23, 59, 59));
		}

		if (month.getValue() >= Month.SEPTEMBER.getValue()) {
			LocalDate endDate = LocalDate.of(year + 1, Month.FEBRUARY,
					LocalDate.of(year + 1, Month.FEBRUARY, 1).lengthOfMonth());
			return new SemesterRange(
					LocalDate.of(year, Month.SEPTEMBER, 1).atStartOfDay(),
					endDate.atTime(23, 59, 59));
		}

		LocalDate endDate = LocalDate.of(year, Month.FEBRUARY, LocalDate.of(year, Month.FEBRUARY, 1).lengthOfMonth());
		return new SemesterRange(
				LocalDate.of(year - 1, Month.SEPTEMBER, 1).atStartOfDay(),
				endDate.atTime(23, 59, 59));
	}

	private SemesterRange getLastSemesterRange(LocalDateTime now) {
		Month month = now.getMonth();
		int year = now.getYear();
		if (month.getValue() >= Month.MARCH.getValue() && month.getValue() <= Month.AUGUST.getValue()) {
			LocalDate endDate = LocalDate.of(year, Month.FEBRUARY,
					LocalDate.of(year, Month.FEBRUARY, 1).lengthOfMonth());
			return new SemesterRange(
					LocalDate.of(year - 1, Month.SEPTEMBER, 1).atStartOfDay(),
					endDate.atTime(23, 59, 59));
		}

		if (month.getValue() >= Month.SEPTEMBER.getValue()) {
			return new SemesterRange(
					LocalDate.of(year, Month.MARCH, 1).atStartOfDay(),
					LocalDate.of(year, Month.AUGUST, 31).atTime(23, 59, 59));
		}

		return new SemesterRange(
				LocalDate.of(year - 1, Month.MARCH, 1).atStartOfDay(),
				LocalDate.of(year - 1, Month.AUGUST, 31).atTime(23, 59, 59));
	}

	private static class SemesterRange {
		private final LocalDateTime startAt;
		private final LocalDateTime endAt;

		public SemesterRange(LocalDateTime startAt, LocalDateTime endAt) {
			this.startAt = startAt;
			this.endAt = endAt;
		}

		public LocalDateTime startAt() {
			return startAt;
		}

		public LocalDateTime endAt() {
			return endAt;
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
