package com.devlink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * DevLink 애플리케이션 메인 클래스
 * 포트폴리오 공유 및 랭킹 플랫폼
 *
 * @since 2026.05.16
 * @version 1.0.0
 * @author 신태훈, 조하겸
 */
@SpringBootApplication
@EnableJpaAuditing
public class DevlinkApplication {

	public static void main(String[] args) {
		SpringApplication.run(DevlinkApplication.class, args);
	}
}
