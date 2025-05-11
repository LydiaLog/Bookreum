package com.bookreum.dev.domain.club.jwt;

import com.bookreum.dev.domain.club.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/kakao/callback")
    public String kakaoLogin(OAuth2AuthenticationToken authentication) {
        String userId = authentication.getName();
        String token = jwtTokenProvider.createToken(userId);
        return "JWT Token: " + token;
    }
}

