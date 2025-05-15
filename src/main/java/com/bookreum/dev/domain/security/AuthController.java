package com.bookreum.dev.domain.security;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * OAuth2 로그인 콜백 엔드포인트
 * 카카오 로그인 성공 후 호출되어 JWT를 생성해 반환합니다.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 카카오 로그인 콜백
     * (application.yml 에 설정된 redirect-uri 와 맞춰주셔야 합니다)
     *
     * @param authentication OAuth2AuthenticationToken 에 담긴 카카오 유저 정보
     * @return JWT 토큰 문자열 (Bearer 없이 순수 토큰)
     */
    @GetMapping("/kakao/callback")
    public String kakaoLogin(OAuth2AuthenticationToken authentication) {
        // 1) OAuth2User 에서 attributes 추출
        DefaultOAuth2User principal = (DefaultOAuth2User) authentication.getPrincipal();
        String kakaoId = principal.getAttribute("id").toString();

        @SuppressWarnings("unchecked")
        Map<String, Object> props = (Map<String, Object>) principal.getAttribute("properties");
        String nickname     = (String) props.get("nickname");
        String profileImage = (String) props.get("profile_image");

        // 2) JWT 생성
        return jwtTokenProvider.createToken(kakaoId, nickname, profileImage);
    }
}
