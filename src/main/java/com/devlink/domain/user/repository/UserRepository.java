package com.devlink.domain.user.repository;

import com.devlink.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 사용자 리포지토리
 *
 * @since 2026.05.16
 * @version 1.0.0
 * @author 신태훈, 조하겸
 */
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByPortalId(String portalId);

	boolean existsByPortalId(String portalId);

	boolean existsByStudentId(String studentId);
}
