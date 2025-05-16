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
    private final CorsFilter corsFilter;
    private final CustomSocialLoginSuccessHandler customSocialSuccessHandler;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomUserDetailsService customUserDetailsService;
    private final DataSource dataSource;
    private final AccessDeniedHandler customAccessDeniedHandler;
    
    /**
     * Remember-Me JDBC 토큰 저장소에 사용할 DataSource
     * Spring Boot가 application.properties의 설정으로 자동 구성합니다.
     */

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt 기반 비밀번호 암호화
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // CSRF 비활성화 (REST API + JWT)
            .csrf(csrf -> csrf.disable())

            // 세션 사용 안 함
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // CORS 필터 적용
            .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)

            // 폼 로그인(소셜 로그인 콜백) 허용
            .formLogin(form -> form
                .loginPage("/user/login")
                .successHandler(customSocialSuccessHandler)
                .permitAll()
            )

            // 로그아웃
            .logout(logout -> logout
                .logoutUrl("/user/logout")
                .deleteCookies("JSESSIONID")
                .logoutSuccessUrl("/user/login?logout")
            )

            // Remember-Me
            .rememberMe(rm -> rm
                .tokenRepository(persistentTokenRepository())
                .userDetailsService(customUserDetailsService)
                .key("rememberKey1234")
                .tokenValiditySeconds(60 * 60 * 24 * 30)
            )

            // OAuth2 로그인 (카카오)
            .oauth2Login(oauth -> oauth
                .loginPage("/user/login")
                .userInfoEndpoint(ui -> ui.userService(customOAuth2UserService))
                .successHandler(customSocialSuccessHandler)
            )

            // 권한 설정
            .authorizeHttpRequests(auth -> auth
                    // 1) 정적 리소스·OAuth·웹소켓 등
                    .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                    .requestMatchers("/user/login", "/api/auth/**", "/ws-chat/**").permitAll()

                    // 2) 북로그(Posts) → 목록, 검색(책 검색 & 컨텐츠 검색), 상세
                    .requestMatchers(HttpMethod.GET,
                        "/",                           // 홈(전체 글 페이징)
                        "/api/posts",                  // 게시글 목록 or 검색(키워드 query param)
                        "/api/posts/searchBooks",      // 책 검색 (키워드 → Aladin API)
                        "/api/posts/**"                // /api/posts/{id} 상세 (단, /api/posts/{id}/heart-count 은 ** 두 단계라 매치 안 됨)
                    ).permitAll()

                    // 3) 북클럽(Clubs) → 목록, 책 검색, 상세
                    .requestMatchers(HttpMethod.GET,
                        "/api/clubs",                  // 클럽 목록
                        "/api/clubs/searchBooks",      // 책 검색 (키워드 → Aladin API)
                        "/api/clubs/**"                // /api/clubs/{id} 상세 (그 이하 /applications/** 은 매치 안 됨)
                    ).permitAll()

                    // 4) 나머지 모든 요청(POST/PUT/DELETE 포함)은 인증된 회원만
                    .anyRequest().authenticated()
                )

            // JWT 인증 필터
            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, customUserDetailsService),
                             UsernamePasswordAuthenticationFilter.class)

            // 403 접근 거부 핸들러
            .exceptionHandling(ex -> ex.accessDeniedHandler(customAccessDeniedHandler));

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
