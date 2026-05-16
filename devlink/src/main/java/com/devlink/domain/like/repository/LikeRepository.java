package com.devlink.domain.like.repository;

import com.devlink.domain.like.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 공감 Repository
 *
 * @since : 2026.05.16
 * @version : 0.0.1
 * @author : DevLink Team
 */
public interface LikeRepository extends JpaRepository<Like, Long> {

	/**
	 * 사용자 ID + 포트폴리오 ID로 공감 조회
	 *
	 * @param userId 사용자 ID
	 * @param portfolioId 포트폴리오 ID
	 * @return Optional<Like>
	 */
	Optional<Like> findByUserIdAndPortfolioId(Long userId, Long portfolioId);

	/**
	 * 공감 여부 확인
	 *
	 * @param userId 사용자 ID
	 * @param portfolioId 포트폴리오 ID
	 * @return 공감 여부
	 */
	boolean existsByUserIdAndPortfolioId(Long userId, Long portfolioId);
}
