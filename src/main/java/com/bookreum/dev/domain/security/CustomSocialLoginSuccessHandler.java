package com.bookreum.dev.domain.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * ✨ 소셜(OAuth2) 로그인 성공 시 호출되는 핸들러  
 *  └ 카카오 프로필을 읽어 JWT 발급 후 **JSON** 으로 반환  
 *  └ 프론트는 `{ token }` 값을 저장한 뒤 화면 라우팅을 수행한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomSocialLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        /* 1️⃣ OAuth2User 정보 추출 */
        OAuth2AuthenticationToken oauth = (OAuth2AuthenticationToken) authentication;
        DefaultOAuth2User principal     = (DefaultOAuth2User) oauth.getPrincipal();

        String kakaoId = principal.getAttribute("id").toString();

        @SuppressWarnings("unchecked")
        Map<String, Object> props = (Map<String, Object>) principal.getAttribute("properties");
        String nickname     = (String) props.get("nickname");
        String profileImage = (String) props.get("profile_image");

        log.debug("[SOCIAL] 카카오 로그인 성공  ▶  id={}, nickname={}", kakaoId, nickname);

        /* 2️⃣ JWT 생성 */
        String accessToken = jwtTokenProvider.createToken(kakaoId, nickname, profileImage);
        log.info("[JWT] 토큰 발급 완료  ▶  subject={}", kakaoId);

        /* 3️⃣ JSON 응답 */
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        String body = "{\"token\":\"" + accessToken + "\"}";
        response.getWriter().write(body);

        /* 4️⃣ 로그 추가 */
        log.debug("[RESPONSE] JWT JSON 전송 완료  ▶  path={}", request.getRequestURI());
    }
}
