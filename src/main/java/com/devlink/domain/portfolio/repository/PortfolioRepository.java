package com.devlink.domain.portfolio.repository;

import com.devlink.domain.portfolio.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 포트폴리오 Repository
 *
 * @since : 2026.05.16
 * @version : 0.0.1
 * @author : DevLink Team
 */
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

	/**
	 * 공감 수 내림차순 → 최신순 정렬 (랭킹 조회)
	 *
	 * @return 랭킹 순 포트폴리오 목록
	 */
	@Query("SELECT p FROM Portfolio p ORDER BY p.likeCount DESC, p.createdAt DESC")
	List<Portfolio> findAllOrderByLikeCountDesc();

	/**
	 * 작성자 ID로 포트폴리오 목록 조회
	 *
	 * @param userId 작성자 ID
	 * @return 포트폴리오 목록
	 */
	List<Portfolio> findByUserId(Long userId);
}
