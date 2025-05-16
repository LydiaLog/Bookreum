package com.bookreum.dev.domain.security;

import io.jsonwebtoken.*;                             // JWT 생성·파싱 라이브러리
import io.jsonwebtoken.security.Keys;                 // HMAC 키 생성 유틸

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;                              // 서명 키 인터페이스
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

/**
 * JWT 토큰 생성·검증을 담당하는 컴포넌트
 */
@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long jwtExpirationMs;

    public JwtTokenProvider(
            SecretKey jwtSigningKey,
            @Value("${jwt.expiration}") long jwtExpirationMs
    ) {
        this.key = jwtSigningKey;
        this.jwtExpirationMs = jwtExpirationMs;
    }

    /**
     * JWT 액세스 토큰 생성
     */
    public String createToken(String kakaoId, String nickname, String profileImage) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("nickname", nickname);
        claims.put("profileImage", profileImage);

        Date now = new Date();
        Date exp = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                   .setClaims(claims)
                   .setSubject(kakaoId)
                   .setIssuedAt(now)
                   .setExpiration(exp)
                   .signWith(key, SignatureAlgorithm.HS512)
                   .compact();
    }

    /**
     * JWT에서 subject(카카오ID) 추출
     */
    public String getKakaoIdFromToken(String token) {
        return Jwts.parserBuilder()
                   .setSigningKey(key)
                   .build()
                   .parseClaimsJws(token)
                   .getBody()
                   .getSubject();
    }

    /**
     * JWT 검증
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
