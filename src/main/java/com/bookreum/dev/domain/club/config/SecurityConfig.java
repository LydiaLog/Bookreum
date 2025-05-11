package com.bookreum.dev.domain.club.config;

import com.bookreum.dev.domain.club.jwt.JwtAuthenticationFilter;
import com.bookreum.dev.domain.club.jwt.JwtTokenProvider;
import com.bookreum.dev.domain.club.jwt.CustomOAuth2UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomOAuth2UserService customOAuth2UserService;

    /**
     * 시큐리티 필터 체인 정의
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // REST/API 튜 토큰 기반이므로 CSRF 비활성화
            .csrf(csrf -> csrf.disable())

            // 세션 사용 안 함 (무상태, Stateless)
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // CORS 필터 먼저 등록
            .addFilterBefore(corsFilter(), UsernamePasswordAuthenticationFilter.class)

            // OAuth2 로그인 설정
            .oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(customOAuth2UserService)
                )
                // 로그인 성공 후 커스텀 핸들러가 토큰을 만들어줌
                .successHandler((request, response, authentication) -> {
                    // 1) 인증된 OAuth2User 로부터 사용자 식별값 가져오기
                    String userId = authentication.getName();
                    String nickname = authentication.getPrincipal().getAttributes().get("nickname").toString();

                    // 2) JWT 토큰 생성
                    String token = jwtTokenProvider.createToken(userId, nickname);

                    // 3) 토큰을 클라이언트에 전달 (JSON or 헤더)
                    response.setHeader("Authorization", "Bearer " + token);
                    response.setStatus(200);
                })
            )

            // 나머지 요청에 대한 접근 제어
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**", "/ws-chat/**").permitAll()  // 로그인, WS 엔드포인트
                .anyRequest().authenticated()
            )

            // JWT 인증 필터 등록 (UsernamePasswordAuthenticationFilter 앞)
            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                             UsernamePasswordAuthenticationFilter.class)
        ;

        return http.build();
    }

    /**
     * 전역 CORS 설정 (모든 엔드포인트 / 모든 메소드 허용)
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(List.of("*"));               // 개발단계: 모든 출처 허용
        cfg.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", cfg);
        return new CorsFilter(src);
    }
}
