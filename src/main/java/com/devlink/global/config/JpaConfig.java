package com.devlink.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA Auditing 활성화 설정
 * BaseEntity 의 createdAt / updatedAt 자동 주입
 *
 * @since : 2026.05.16
 * @version : 0.0.1
 * @author : DevLink Team
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}
