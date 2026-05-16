package com.devlink.domain.user.repository;

import com.devlink.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 사용자 Repository
 *
 * @since : 2026.05.16
 * @version : 0.0.1
 * @author : DevLink Team
 */
public interface UserRepository extends JpaRepository<User, Long> {

	/**
	 * 이메일로 사용자 조회
	 *
	 * @param email 이메일
	 * @return Optional<User>
	 */
	Optional<User> findByEmail(String email);

	/**
	 * 학번으로 사용자 조회
	 *
	 * @param studentId 학번
	 * @return Optional<User>
	 */
	Optional<User> findByStudentId(String studentId);

	/**
	 * 이메일 중복 확인
	 *
	 * @param email 이메일
	 * @return 중복 여부
	 */
	boolean existsByEmail(String email);
}
