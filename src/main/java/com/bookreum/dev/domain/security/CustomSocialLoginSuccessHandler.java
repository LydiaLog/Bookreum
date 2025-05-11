package com.bookreum.dev.domain.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CustomSocialLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        DefaultOAuth2User user = (DefaultOAuth2User) oauthToken.getPrincipal();

        String kakaoId      = user.getAttribute("id").toString();
        String nickname     = ((Map<String,Object>)user.getAttribute("properties")).get("nickname").toString();
        String profileImage = ((Map<String,Object>)user.getAttribute("properties")).get("profile_image").toString();

        // 새 createToken 호출: 카카오ID, 닉네임, 프로필 이미지
        String token = jwtTokenProvider.createToken(kakaoId, nickname, profileImage);

        response.setHeader("Authorization", "Bearer " + token);
        response.sendRedirect("/");
    }

}
