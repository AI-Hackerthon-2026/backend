package com.devlink.domain.skill.repository;

import com.devlink.domain.skill.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 기술스택 리포지토리
 *
 * @since 2026.05.16
 * @version 1.0.0
 * @author 신태훈, 조하겸
 */
public interface SkillRepository extends JpaRepository<Skill, Long> {

	Optional<Skill> findByName(String name);

	List<Skill> findByNameContainingIgnoreCase(String q);

	List<Skill> findByCategory(String category);

	List<Skill> findByNameContainingIgnoreCaseAndCategory(String q, String category);
}
