package com.bookreum.dev.domain.club.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * ✅ CORS 설정: 다른 네트워크 장치에서도 접속 가능하도록 허용
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // ✅ 모든 경로에 대해 CORS 허용
                .allowedOrigins("http://localhost:5173", "http://10.50.216.86.*:5173") // ✅ 허용할 도메인 (프론트엔드 IP)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // ✅ 허용할 HTTP 메서드
                .allowedHeaders("*") // ✅ 모든 헤더 허용
                .allowCredentials(true); // ✅ 쿠키, 인증 정보 허용
    }
}
