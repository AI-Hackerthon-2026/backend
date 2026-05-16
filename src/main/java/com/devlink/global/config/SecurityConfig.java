package com.devlink.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Spring Security 설정 클래스
 * JWT 없이 HttpSession 기반 인증 사용
 * CSRF 비활성화 (REST API)
 *
 * @since 2026.05.16
 * @version 1.0.0
 * @author DevLink Team
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			// REST API이므로 CSRF 비활성화
			.csrf(AbstractHttpConfigurer::disable)

			// 세션 정책: 필요 시 생성
			.sessionManagement(session ->
				session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))

			// 경로별 접근 권한 설정
			.authorizeHttpRequests(auth -> auth
				// 인증 불필요 경로
				.requestMatchers("/api/auth/login", "/api/auth/register").permitAll()
				.requestMatchers(HttpMethod.GET, "/api/portfolios/**").permitAll()
				.requestMatchers(HttpMethod.GET, "/api/skills/**").permitAll()
				.requestMatchers("/images/**").permitAll()
				.requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/api-docs/**").permitAll()
				.requestMatchers("/h2-console/**").permitAll()
				// 나머지는 인증 필요
				.anyRequest().authenticated())

			// H2 콘솔 iframe 허용
			.headers(headers ->
				headers.frameOptions(frame -> frame.sameOrigin()))

			// 미인증 접근 → 401 JSON 응답
			.exceptionHandling(ex -> ex
				.authenticationEntryPoint((request, response, authException) -> {
					response.setStatus(HttpStatus.UNAUTHORIZED.value());
					response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
					Map<String, Object> body = new LinkedHashMap<>();
					body.put("success", false);
					body.put("message", "로그인이 필요한 서비스입니다.");
					body.put("data", null);
					response.getWriter().write(objectMapper.writeValueAsString(body));
				})
				.accessDeniedHandler((request, response, accessDeniedException) -> {
					response.setStatus(HttpStatus.FORBIDDEN.value());
					response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
					Map<String, Object> body = new LinkedHashMap<>();
					body.put("success", false);
					body.put("message", "접근 권한이 없습니다.");
					body.put("data", null);
					response.getWriter().write(objectMapper.writeValueAsString(body));
				}))

			// 로그아웃 설정
			.logout(logout -> logout
				.logoutUrl("/api/auth/logout")
				.invalidateHttpSession(true)
				.clearAuthentication(true)
				.deleteCookies("JSESSIONID")
				.logoutSuccessHandler((request, response, authentication) -> {
					response.setStatus(HttpStatus.OK.value());
					response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
					Map<String, Object> body = new LinkedHashMap<>();
					body.put("success", true);
					body.put("message", "로그아웃되었습니다.");
					body.put("data", null);
					response.getWriter().write(objectMapper.writeValueAsString(body));
				}));

		return http.build();
	}
}
