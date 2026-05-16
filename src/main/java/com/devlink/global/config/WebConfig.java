package com.devlink.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 정적 리소스 핸들러 설정
 * 런타임에 업로드된 이미지를 /image/** URL로 즉시 서빙하기 위해
 * 파일 시스템 절대 경로를 리소스 위치로 등록
 *
 * @since 2026.05.17
 * @version 1.0.0
 * @author 최준혁
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Value("${file.upload.dir}")
	private String uploadDir;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/image/**")
			.addResourceLocations(uploadDir);
	}
}