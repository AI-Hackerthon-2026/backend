package com.devlink.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger (SpringDoc OpenAPI) 설정 클래스
 *
 * @since 2026.05.16
 * @version 1.0.0
 * @author DevLink Team
 */
@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI()
			.info(new Info()
				.title("DevLink API")
				.description("포트폴리오 공유 및 랭킹 플랫폼 DevLink REST API 명세서")
				.version("v1.0.0"));
	}
}
