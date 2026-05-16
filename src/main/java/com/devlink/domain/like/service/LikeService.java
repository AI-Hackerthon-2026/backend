package com.devlink.domain.like.service;

import com.devlink.domain.like.entity.Like;
import com.devlink.domain.like.repository.LikeRepository;
import com.devlink.domain.portfolio.entity.Portfolio;
import com.devlink.domain.portfolio.repository.PortfolioRepository;
import com.devlink.domain.user.entity.User;
import com.devlink.domain.user.repository.UserRepository;
import com.devlink.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 공감 서비스
 * 한 사용자는 하나의 포트폴리오에 한 번만 공감 가능
 *
 * @since : 2026.05.16
 * @version : 0.0.1
 * @author : DevLink Team
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeService {

	private final LikeRepository likeRepository;
	private final PortfolioRepository portfolioRepository;
	private final UserRepository userRepository;

	/**
	 * 포트폴리오 공감 추가
	 *
	 * @param userId 사용자 ID
	 * @param portfolioId 포트폴리오 ID
	 * @throws CustomException 이미 공감한 경우 예외 발생
	 */
	@Transactional
	public void addLike(Long userId, Long portfolioId) {
		/* 중복 공감 방지 */
		if (likeRepository.existsByUserIdAndPortfolioId(userId, portfolioId)) {
			throw CustomException.ALREADY_LIKED;
		}

		User user = userRepository.findById(userId)
			.orElseThrow(() -> CustomException.NOT_FOUND);

		Portfolio portfolio = portfolioRepository.findById(portfolioId)
			.orElseThrow(() -> CustomException.NOT_FOUND);

		Like like = Like.builder()
			.user(user)
			.portfolio(portfolio)
			.build();

		likeRepository.save(like);

		/* 포트폴리오 공감 수 증가 */
		portfolio.increaseLikeCount();
	}

	/**
	 * 포트폴리오 공감 취소
	 *
	 * @param userId 사용자 ID
	 * @param portfolioId 포트폴리오 ID
	 */
	@Transactional
	public void cancelLike(Long userId, Long portfolioId) {
		Like like = likeRepository.findByUserIdAndPortfolioId(userId, portfolioId)
			.orElseThrow(() -> CustomException.NOT_FOUND);

		Portfolio portfolio = portfolioRepository.findById(portfolioId)
			.orElseThrow(() -> CustomException.NOT_FOUND);

		likeRepository.delete(like);

		/* 포트폴리오 공감 수 감소 */
		portfolio.decreaseLikeCount();
	}
}
