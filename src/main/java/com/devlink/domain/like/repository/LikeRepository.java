package com.devlink.domain.like.repository;

import com.devlink.domain.like.entity.Like;
import com.devlink.domain.portfolio.entity.Portfolio;
import com.devlink.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 공감 리포지토리
 *
 * @since 2026.05.16
 * @version 1.0.0
 * @author 신태훈, 조하겸
 */
public interface LikeRepository extends JpaRepository<Like, Long> {

	Optional<Like> findByUserAndPortfolio(User user, Portfolio portfolio);

	boolean existsByUserAndPortfolio(User user, Portfolio portfolio);
}
