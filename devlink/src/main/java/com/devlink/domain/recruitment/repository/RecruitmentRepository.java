package com.devlink.domain.recruitment.repository;

import com.devlink.domain.recruitment.entity.Recruitment;
import com.devlink.global.common.enums.RecruitmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 프로젝트 팀원 모집 Repository
 *
 * @since : 2026.05.16
 * @version : 0.0.1
 * @author : DevLink Team
 */
public interface RecruitmentRepository extends JpaRepository<Recruitment, Long> {

	/**
	 * 모집 상태로 목록 조회
	 *
	 * @param status 모집 상태
	 * @return 모집 글 목록
	 */
	List<Recruitment> findByStatus(RecruitmentStatus status);

	/**
	 * 작성자 ID로 모집 글 목록 조회
	 *
	 * @param userId 작성자 ID
	 * @return 모집 글 목록
	 */
	List<Recruitment> findByUserId(Long userId);
}
