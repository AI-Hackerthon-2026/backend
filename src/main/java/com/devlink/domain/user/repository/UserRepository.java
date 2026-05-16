package com.devlink.domain.user.repository;

import com.devlink.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
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

	/** 이름 또는 학번으로 사용자 검색 (LIKE 검색, 본인 제외) */
	@Query("SELECT u FROM User u WHERE (u.name LIKE %:q% OR u.studentId LIKE %:q%) AND u.id <> :excludeUserId")
	List<User> searchByNameOrStudentId(@Param("q") String q, @Param("excludeUserId") Long excludeUserId);
}
