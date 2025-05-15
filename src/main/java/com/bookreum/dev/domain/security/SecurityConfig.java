package com.bookreum.dev.domain.security;

import com.bookreum.dev.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;  // Spring Boot 자동 구성되는 DataSource 사용
import java.util.Arrays;
import java.util.Collections;

/**
 * 스프링 시큐리티 설정 클래스
 * - JWT, OAuth2, 세션, CORS, Remember-Me 등을 구성합니다.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomSocialLoginSuccessHandler customSocialSuccessHandler;
    private final CustomUserDetailsService customUserDetailsService;
    
    /**
     * Remember-Me JDBC 토큰 저장소에 사용할 DataSource
     * Spring Boot가 application.properties의 설정으로 자동 구성합니다.
     */
    private final DataSource dataSource;

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt 기반 비밀번호 암호화
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // CSRF 비활성화 (REST+JWT 환경)
            .csrf(csrf -> csrf.disable())

            // 세션은 필요할 때만 생성
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))

            // CORS 필터 등록
            .addFilterBefore(corsFilter(), UsernamePasswordAuthenticationFilter.class)

            // 인증 실패(401), 인가 실패(403) 핸들링
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, ex2) -> {
                    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    res.setContentType("application/json;charset=UTF-8");
                    res.getWriter().write("{\"error\":\"인증이 필요합니다.\"}");
                })
                .accessDeniedHandler(customAccessDeniedHandler())
            )

            // 커스텀 UserDetailsService 등록
            .userDetailsService(customUserDetailsService)

            // 폼 로그인 설정
            .formLogin(form -> form
                .loginPage("/user/login")
                .loginProcessingUrl("/user/login")
                .successHandler(customSocialSuccessHandler)
                .permitAll()
            )

            // 로그아웃 설정
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/user/logout"))
                .deleteCookies("JSESSIONID")
                .logoutSuccessUrl("/user/login?logout")
            )

            // Remember-Me 설정
            .rememberMe(rm -> rm
                .key("rememberKey1234")
                .tokenValiditySeconds(60 * 60 * 24 * 30)
                .tokenRepository(persistentTokenRepository())
                .userDetailsService(customUserDetailsService)
            )

            // OAuth2 로그인 (카카오)
            .oauth2Login(oauth -> oauth
                .loginPage("/user/login")
                .userInfoEndpoint(ui -> ui.userService(customOAuth2UserService))
                .successHandler(customSocialSuccessHandler)
            )

            // URL 접근 권한 설정
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                .requestMatchers("/user/login", "/api/auth/**", "/ws-chat/**").permitAll()
                .anyRequest().authenticated()
            )

            // JWT 인증 필터 등록
            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, customUserDetailsService),
                             UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
 // CORS 설정: 로컬 개발 환경에서 모든 오리진 허용, 자격증명 허용
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOriginPatterns(Collections.singletonList("*"));
        cfg.setAllowedMethods(Arrays.asList("GET","POST","PUT","DELETE","OPTIONS"));
        cfg.setAllowedHeaders(Collections.singletonList("*"));
        cfg.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", cfg);
        return new CorsFilter(src);
    }
    // Remember-Me 토큰을 JDBC로 저장
    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
        repo.setDataSource(dataSource);
        return repo;
    }
    // 403 Forbidden 처리 핸들러
    @Bean
    public AccessDeniedHandler customAccessDeniedHandler() {
        return new Custom403Handler();
    }
}
