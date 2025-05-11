package com.bookreum.dev.domain.security;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;

@Configuration  // 스프링 Security 설정을 위한 설정 클래스임을 선언
@EnableWebSecurity  // 웹 보안 기능 활성화
@EnableMethodSecurity(prePostEnabled = true)  // 메서드 기반 보안(Pre/Post 어노테이션) 활성화
@RequiredArgsConstructor  // final 필드를 생성자 주입
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomSocialLoginSuccessHandler customSocialSuccessHandler;
    private final CustomUserDetailsService customUserDetailsService;  // Remember-Me용 서비스
    private final DataSource dataSource;

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 비밀번호 암호화를 위한 BCryptPasswordEncoder 빈 등록
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // CSRF 비활성화 (REST API + JWT 사용 시)
            .csrf(csrf -> csrf.disable())

            // 세션을 사용하지 않도록 설정
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // CORS 필터를 UsernamePasswordAuthenticationFilter 이전에 등록
            .addFilterBefore(corsFilter(), UsernamePasswordAuthenticationFilter.class)

            // 폼 로그인 설정 (커스텀 로그인 페이지 및 성공 핸들러)
            .formLogin(form -> form
                .loginPage("/user/login")
                .successHandler(customSocialSuccessHandler)
                .permitAll()  // 로그인 페이지는 모두 접근 허용
            )

            // 로그아웃 설정
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/user/logout"))
                .deleteCookies("JSESSIONID")
                .logoutSuccessUrl("/user/login?logout")
            )

            // Remember-Me 설정
            .rememberMe(rm -> rm
                .key("rememberKey1234")  // 토큰 생성 키
                .tokenValiditySeconds(60 * 60 * 24 * 30)  // 30일 유효
                .tokenRepository(persistentTokenRepository())  // JDBC 토큰 저장소
                .userDetailsService(customUserDetailsService)  // 사용자 조회 서비스
            )

            // OAuth2 로그인 설정 (카카오)
            .oauth2Login(oauth -> oauth
                .loginPage("/user/login")
                .userInfoEndpoint(ui -> ui
                    .userService(customOAuth2UserService)  // 사용자 정보 가져오는 서비스
                )
                .successHandler(customSocialSuccessHandler)  // 로그인 성공 핸들러
            )

            // URL별 접근 권한 설정
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()  // 정적 리소스
                .requestMatchers("/user/login", "/api/auth/**", "/ws-chat/**").permitAll()  // 로그인/인증 API
                .anyRequest().authenticated()  // 그 외 모든 요청 인증 필요
            )

            // JWT 인증 필터 등록
            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)

            // 접근 거부 및 예외 처리 핸들러
            .exceptionHandling(ex -> ex.accessDeniedHandler(customAccessDeniedHandler()));

        return http.build();
    }

    @Bean
    public CorsFilter corsFilter() {
        // 전역 CORS 설정
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(List.of("*"));  // 모든 출처 허용
        cfg.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));  // 허용 HTTP 메서드
        cfg.setAllowedHeaders(List.of("*"));  // 모든 헤더 허용
        cfg.setAllowCredentials(true);  // 인증 정보 허용

        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", cfg);
        return new CorsFilter(src);
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        // Remember-Me 토큰을 JDBC로 저장
        JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
        repo.setDataSource(dataSource);
        return repo;
    }

    @Bean
    public AccessDeniedHandler customAccessDeniedHandler() {
        // 403 Forbidden 처리 핸들러
        return new Custom403Handler();
    }
}