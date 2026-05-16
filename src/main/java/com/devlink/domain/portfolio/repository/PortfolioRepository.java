package com.devlink.domain.portfolio.repository;

import com.devlink.domain.portfolio.entity.Portfolio;
import com.devlink.domain.portfolio.entity.PortfolioCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 포트폴리오 리포지토리
 *
 * @since 2026.05.16
 * @version 1.0.0
 * @author 신태훈, 조하겸
 */
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

	/** 삭제되지 않은 포트폴리오 조회 */
	Optional<Portfolio> findByIdAndIsDeletedFalse(Long id);

	/** 기술 필터 없이 전체 목록 (최신순) */
	Page<Portfolio> findAllByIsDeletedFalseOrderByCreatedAtDesc(Pageable pageable);

	/** 기술 필터 없이 전체 목록 (공감 많은 순) */
	Page<Portfolio> findAllByIsDeletedFalseOrderByLikeCountDescCreatedAtDesc(Pageable pageable);

	/** 기술 필터 + 카테고리 + 최신순 */
	Page<Portfolio> findAllByCategoryAndIsDeletedFalseOrderByCreatedAtDesc(
			PortfolioCategory category, Pageable pageable);

	/** 기술 필터 + 카테고리 + 공감 많은 순 */
	Page<Portfolio> findAllByCategoryAndIsDeletedFalseOrderByLikeCountDescCreatedAtDesc(
			PortfolioCategory category, Pageable pageable);

	/** 기술스택으로 필터링한 목록 */
	@Query(value = "SELECT DISTINCT p FROM Portfolio p " +
			"JOIN PortfolioSkill ps ON ps.portfolio = p " +
			"JOIN Skill s ON ps.skill = s " +
			"WHERE p.isDeleted = false AND (:category IS NULL OR p.category = :category) " +
			"AND s.name IN :skillNames " +
			"ORDER BY p.createdAt DESC", countQuery = "SELECT COUNT(DISTINCT p) FROM Portfolio p " +
					"JOIN PortfolioSkill ps ON ps.portfolio = p " +
					"JOIN Skill s ON ps.skill = s " +
					"WHERE p.isDeleted = false AND (:category IS NULL OR p.category = :category) " +
					"AND s.name IN :skillNames")
	Page<Portfolio> findBySkillsAndLatest(
			@Param("category") PortfolioCategory category,
			@Param("skillNames") List<String> skillNames,
			Pageable pageable);

	@Query(value = "SELECT DISTINCT p FROM Portfolio p " +
			"JOIN PortfolioSkill ps ON ps.portfolio = p " +
			"JOIN Skill s ON ps.skill = s " +
			"WHERE p.isDeleted = false AND (:category IS NULL OR p.category = :category) " +
			"AND s.name IN :skillNames " +
			"ORDER BY p.likeCount DESC, p.createdAt DESC", countQuery = "SELECT COUNT(DISTINCT p) FROM Portfolio p " +
					"JOIN PortfolioSkill ps ON ps.portfolio = p " +
					"JOIN Skill s ON ps.skill = s " +
					"WHERE p.isDeleted = false AND (:category IS NULL OR p.category = :category) " +
					"AND s.name IN :skillNames")
	Page<Portfolio> findBySkillsAndLikes(
			@Param("category") PortfolioCategory category,
			@Param("skillNames") List<String> skillNames,
			Pageable pageable);

	/** 랭킹 (공감 많은 순, 최대 N개) */
	List<Portfolio> findTop3ByIsDeletedFalseOrderByLikeCountDescCreatedAtDesc();

	/** 전체 기간 랭킹 조회 */
	List<Portfolio> findAllByIsDeletedFalseOrderByLikeCountDescCreatedAtDesc();

	/** 특정 기간 랭킹 조회 (end_date 기준) */
	List<Portfolio> findAllByIsDeletedFalseAndEndDateBetweenOrderByLikeCountDescCreatedAtDesc(
			LocalDate startDate, LocalDate endDate);

	Optional<Portfolio> findTopByIsDeletedFalseAndEndDateBetweenOrderByLikeCountDescCreatedAtDesc(
			LocalDate startDate, LocalDate endDate);

	Optional<Portfolio> findTopByIsDeletedFalseOrderByLikeCountDescCreatedAtDesc();

	/** GitHub 링크 중복 확인 */
	boolean existsByGithubLinkAndIsDeletedFalse(String githubLink);

	boolean existsByGithubLinkAndIsDeletedFalseAndIdNot(String githubLink, Long id);
}
