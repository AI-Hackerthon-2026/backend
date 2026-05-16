package com.devlink.domain.portfolio.repository;

import com.devlink.domain.portfolio.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
	List<Portfolio> findAllByIsDeletedFalseOrderByCreatedAtDesc();

	/** 기술 필터 없이 전체 목록 (공감 많은 순) */
	List<Portfolio> findAllByIsDeletedFalseOrderByLikeCountDescCreatedAtDesc();

	/** 기술스택으로 필터링한 목록 */
	@Query("SELECT DISTINCT p FROM Portfolio p " +
		"JOIN PortfolioSkill ps ON ps.portfolio = p " +
		"JOIN Skill s ON ps.skill = s " +
		"WHERE p.isDeleted = false AND s.name IN :skillNames " +
		"ORDER BY p.createdAt DESC")
	List<Portfolio> findBySkillsAndLatest(@Param("skillNames") List<String> skillNames);

	@Query("SELECT DISTINCT p FROM Portfolio p " +
		"JOIN PortfolioSkill ps ON ps.portfolio = p " +
		"JOIN Skill s ON ps.skill = s " +
		"WHERE p.isDeleted = false AND s.name IN :skillNames " +
		"ORDER BY p.likeCount DESC, p.createdAt DESC")
	List<Portfolio> findBySkillsAndLikes(@Param("skillNames") List<String> skillNames);

	/** 랭킹 (공감 많은 순, 최대 N개) */
	List<Portfolio> findTop3ByIsDeletedFalseOrderByLikeCountDescCreatedAtDesc();

	/** GitHub 링크 중복 확인 */
	boolean existsByGithubLinkAndIsDeletedFalse(String githubLink);

	boolean existsByGithubLinkAndIsDeletedFalseAndIdNot(String githubLink, Long id);
}
