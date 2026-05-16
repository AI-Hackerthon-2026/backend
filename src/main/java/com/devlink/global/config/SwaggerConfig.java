package com.devlink.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger (SpringDoc OpenAPI) 설정
 * API 문서 자동 생성 설정
 *
 * @since : 2026.05.16
 * @version : 0.0.1
 * @author : DevLink Team
 */
@Configuration
public class SwaggerConfig {

	/**
	 * OpenAPI 기본 정보 설정
	 *
	 * @return OpenAPI 설정 객체
	 */
	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI()
			.info(new Info()
				.title("DevLink API")
				.description("컴퓨터공학과 학생 전용 커뮤니티 플랫폼 API 문서")
				.version("v0.0.1")
			);
	}
}
