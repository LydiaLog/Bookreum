package com.bookreum.dev.domain.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenProvider {

    private final Key key;
    private final long jwtExpirationMs;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secretKeyBase64,
            @Value("${jwt.expiration}") long jwtExpirationMs) {
        byte[] decoded = Base64.getDecoder().decode(secretKeyBase64);
        this.key = Keys.hmacShaKeyFor(decoded);
        this.jwtExpirationMs = jwtExpirationMs;
    }

    // 카카오 ID (UUID)로 토큰 생성
    public String createToken(String kakaoId, String nickname, String profileImage) {
        Map<String, Object> claims = new HashMap<>();
        // 커스텀 클레임 추가
        claims.put("nickname", nickname);
        claims.put("profileImage", profileImage);

        Date now = new Date();
        Date exp = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                   .setClaims(claims)           // 여기에 클레임을 실어 보냄
                   .setSubject(kakaoId)          // 표준 클레임(subject)
                   .setIssuedAt(now)             // 표준 클레임(iat)
                   .setExpiration(exp)           // 표준 클레임(exp)
                   .signWith(key, SignatureAlgorithm.HS512)
                   .compact();
    }

    public String getKakaoIdFromToken(String token) {
        return Jwts.parserBuilder()
                   .setSigningKey(key)
                   .build()
                   .parseClaimsJws(token)
                   .getBody()
                   .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}