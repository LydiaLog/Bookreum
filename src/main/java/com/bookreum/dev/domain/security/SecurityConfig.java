package com.bookreum.dev.domain.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.filter.CorsFilter;
import org.springframework.http.HttpMethod;

import javax.sql.DataSource;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    // JwtTokenProvider는 지연 주입으로 순환 참조 방지
    private JwtTokenProvider jwtTokenProvider;

    // 아래 필드들은 생성자 주입(@RequiredArgsConstructor)으로 들어옵니다
    private final CorsFilter corsFilter;
    private final CustomSocialLoginSuccessHandler customSocialSuccessHandler;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomUserDetailsService customUserDetailsService;
    private final DataSource dataSource;
    private final AccessDeniedHandler customAccessDeniedHandler;

    @Autowired
    public void setJwtTokenProvider(@Lazy JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // CSRF 비활성화
            .csrf(csrf -> csrf.disable())
            // 세션 사용 안 함
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 분리된 CorsFilter 적용
            .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
            // 폼 로그인
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
            // Remember-Me 설정
            .rememberMe(rm -> rm
                .tokenRepository(persistentTokenRepository())
                .userDetailsService(customUserDetailsService)
                .key("rememberKey1234")
                .tokenValiditySeconds(60 * 60 * 24 * 30)
            )
            // OAuth2 로그인
            .oauth2Login(oauth -> oauth
                .loginPage("/user/login")
                .userInfoEndpoint(ui -> ui.userService(customOAuth2UserService))
                .successHandler(customSocialSuccessHandler)
            )
            // 권한 설정
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                .requestMatchers("/user/login", "/api/auth/**", "/ws-chat/**").permitAll()
                .requestMatchers(HttpMethod.GET,
                    "/", "/api/posts", "/api/posts/searchBooks", "/api/posts/**"
                ).permitAll()
                .requestMatchers(HttpMethod.GET,
                    "/api/clubs", "/api/clubs/searchBooks", "/api/clubs/**"
                ).permitAll()
                .anyRequest().authenticated()
            )
            // JWT 인증 필터
            .addFilterBefore(
                new JwtAuthenticationFilter(jwtTokenProvider, customUserDetailsService),
                UsernamePasswordAuthenticationFilter.class
            )
            // 403 처리 핸들러
            .exceptionHandling(ex -> ex.accessDeniedHandler(customAccessDeniedHandler));

        return http.build();
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
        repo.setDataSource(dataSource);
        return repo;
    }

    @Bean
    public AccessDeniedHandler customAccessDeniedHandler() {
        return new Custom403Handler();
    }
}
