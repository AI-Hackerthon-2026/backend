package com.devlink.domain.skill.repository;

import com.devlink.domain.portfolio.entity.Portfolio;
import com.devlink.domain.skill.entity.PortfolioSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 포트폴리오-기술 매핑 리포지토리
 *
 * @since 2026.05.16
 * @version 1.0.0
 * @author 신태훈, 조하겸
 */
public interface PortfolioSkillRepository extends JpaRepository<PortfolioSkill, Long> {

	List<PortfolioSkill> findAllByPortfolio(Portfolio portfolio);

	@Modifying
	@Query("DELETE FROM PortfolioSkill ps WHERE ps.portfolio = :portfolio")
	void deleteAllByPortfolio(@Param("portfolio") Portfolio portfolio);
}
