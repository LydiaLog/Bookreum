package com.bookreum.dev.domain.security;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/kakao/callback")
    public String kakaoLogin(OAuth2AuthenticationToken authentication) {
        // 인증된 OAuth2User에서 카카오 고유 ID, 닉네임, 프로필 이미지 추출
        DefaultOAuth2User principal = (DefaultOAuth2User) authentication.getPrincipal();
        String kakaoId = principal.getAttribute("id").toString();

        @SuppressWarnings("unchecked")
        Map<String, Object> props = (Map<String, Object>) principal.getAttribute("properties");
        String nickname     = (String) props.get("nickname");
        String profileImage = (String) props.get("profile_image");

        // JWT 토큰 생성 (kakaoId, nickname, profileImage 포함)
        return jwtTokenProvider.createToken(kakaoId, nickname, profileImage);
    }
}
