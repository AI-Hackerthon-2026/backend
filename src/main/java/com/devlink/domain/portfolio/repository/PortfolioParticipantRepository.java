package com.devlink.domain.portfolio.repository;

import com.devlink.domain.portfolio.entity.Portfolio;
import com.devlink.domain.portfolio.entity.PortfolioParticipant;
import com.devlink.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 포트폴리오 참여자 리포지토리
 *
 * @since 2026.05.16
 * @version 1.0.0
 * @author 신태훈, 조하겸
 */
public interface PortfolioParticipantRepository extends JpaRepository<PortfolioParticipant, Long> {

	/** 특정 포트폴리오의 모든 참여자 조회 */
	List<PortfolioParticipant> findAllByPortfolio(Portfolio portfolio);

	/** 사용자가 참여자인 포트폴리오 목록 */
	List<PortfolioParticipant> findAllByUser(User user);

	/** 특정 사용자의 특정 포트폴리오 참여 정보 조회 */
	Optional<PortfolioParticipant> findByPortfolioAndUser(Portfolio portfolio, User user);

	/** 수정 권한 여부 확인 */
	boolean existsByPortfolioAndUserAndCanEditTrue(Portfolio portfolio, User user);

	/** 소유자 여부 확인 */
	boolean existsByPortfolioAndUserAndIsOwnerTrue(Portfolio portfolio, User user);

	/** 포트폴리오 참여자 전체 삭제 */
	@Modifying
	@Query("DELETE FROM PortfolioParticipant pp WHERE pp.portfolio = :portfolio")
	void deleteAllByPortfolio(@Param("portfolio") Portfolio portfolio);
}
