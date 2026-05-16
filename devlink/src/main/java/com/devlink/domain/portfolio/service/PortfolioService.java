package com.devlink.domain.portfolio.service;

import com.devlink.domain.portfolio.dto.PortfolioRequestDto;
import com.devlink.domain.portfolio.dto.PortfolioResponseDto;
import com.devlink.domain.portfolio.entity.Portfolio;
import com.devlink.domain.portfolio.repository.PortfolioRepository;
import com.devlink.domain.user.entity.User;
import com.devlink.domain.user.repository.UserRepository;
import com.devlink.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 포트폴리오 서비스
 *
 * @since : 2026.05.16
 * @version : 0.0.1
 * @author : DevLink Team
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PortfolioService {

	private final PortfolioRepository portfolioRepository;
	private final UserRepository userRepository;

	/**
	 * 포트폴리오 등록
	 *
	 * @param requestDto 포트폴리오 요청 정보
	 * @return 등록된 포트폴리오 응답 DTO
	 */
	@Transactional
	public PortfolioResponseDto createPortfolio(PortfolioRequestDto requestDto) {
		User user = userRepository.findById(requestDto.getUserId())
			.orElseThrow(() -> CustomException.NOT_FOUND);

		Portfolio portfolio = Portfolio.builder()
			.title(requestDto.getTitle())
			.summary(requestDto.getSummary())
			.description(requestDto.getDescription())
			.thumbnailUrl(requestDto.getThumbnailUrl())
			.githubUrl(requestDto.getGithubUrl())
			.deployUrl(requestDto.getDeployUrl())
			.techStack(requestDto.getTechStack())
			.teamSize(requestDto.getTeamSize())
			.myRole(requestDto.getMyRole())
			.startDate(requestDto.getStartDate())
			.endDate(requestDto.getEndDate())
			.user(user)
			.build();

		return PortfolioResponseDto.from(portfolioRepository.save(portfolio));
	}

	/**
	 * 포트폴리오 전체 목록 조회
	 *
	 * @return 포트폴리오 목록
	 */
	public List<PortfolioResponseDto> getAllPortfolios() {
		return portfolioRepository.findAll()
			.stream()
			.map(PortfolioResponseDto::from)
			.collect(Collectors.toList());
	}

	/**
	 * 포트폴리오 단건 조회
	 *
	 * @param portfolioId 포트폴리오 ID
	 * @return 포트폴리오 응답 DTO
	 */
	public PortfolioResponseDto getPortfolioById(Long portfolioId) {
		Portfolio portfolio = portfolioRepository.findById(portfolioId)
			.orElseThrow(() -> CustomException.NOT_FOUND);

		return PortfolioResponseDto.from(portfolio);
	}

	/**
	 * 공감 수 기반 랭킹 조회
	 *
	 * @return 공감 수 내림차순 포트폴리오 목록
	 */
	public List<PortfolioResponseDto> getRanking() {
		return portfolioRepository.findAllOrderByLikeCountDesc()
			.stream()
			.map(PortfolioResponseDto::from)
			.collect(Collectors.toList());
	}

	/**
	 * 포트폴리오 삭제
	 *
	 * @param portfolioId 포트폴리오 ID
	 */
	@Transactional
	public void deletePortfolio(Long portfolioId) {
		Portfolio portfolio = portfolioRepository.findById(portfolioId)
			.orElseThrow(() -> CustomException.NOT_FOUND);

		portfolioRepository.delete(portfolio);
	}
}
