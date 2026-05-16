package com.devlink.domain.like.service;

import com.devlink.domain.like.dto.LikeResponse;
import com.devlink.domain.like.entity.Like;
import com.devlink.domain.like.repository.LikeRepository;
import com.devlink.domain.portfolio.entity.Portfolio;
import com.devlink.domain.portfolio.repository.PortfolioRepository;
import com.devlink.domain.user.entity.User;
import com.devlink.domain.user.repository.UserRepository;
import com.devlink.global.exception.CustomException;
import com.devlink.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 공감 서비스
 * 공감 토글 (있으면 취소, 없으면 추가)
 *
 * @since 2026.05.16
 * @version 1.0.0
 * @author 신태훈, 조하겸
 */
@Service
@RequiredArgsConstructor
public class LikeService {

	private final LikeRepository likeRepository;
	private final PortfolioRepository portfolioRepository;
	private final UserRepository userRepository;

	/**
	 * 공감 토글
	 * 이미 공감했으면 취소, 아니면 추가
	 *
	 * @param portfolioId 포트폴리오 ID
	 * @param userId      현재 로그인 사용자 ID
	 * @return 공감 여부 및 현재 공감 수
	 */
	@Transactional
	public LikeResponse toggleLike(Long portfolioId, Long userId) {
		Portfolio portfolio = portfolioRepository.findByIdAndIsDeletedFalse(portfolioId)
			.orElseThrow(() -> new CustomException(ErrorCode.PORTFOLIO_NOT_FOUND));
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		Optional<Like> existingLike = likeRepository.findByUserAndPortfolio(user, portfolio);

		boolean isLiked;

		if (existingLike.isPresent()) {
			/* 이미 공감한 경우 → 취소 */
			likeRepository.delete(existingLike.get());
			portfolio.decreaseLikeCount();
			isLiked = false;
		} else {
			/* 공감하지 않은 경우 → 추가 */
			likeRepository.save(Like.builder()
				.user(user)
				.portfolio(portfolio)
				.build());
			portfolio.increaseLikeCount();
			isLiked = true;
		}

		return LikeResponse.builder()
			.isLiked(isLiked)
			.likeCount(portfolio.getLikeCount())
			.build();
	}
}
