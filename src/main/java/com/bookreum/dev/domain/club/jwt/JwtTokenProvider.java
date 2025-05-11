package com.bookreum.dev.domain.club.jwt;

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

    @Value("${jwt.secret}")
    private String secretKeyBase64;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    private final Key key;

    // 생성자에서 바로 Base64 디코딩 후 키 초기화
    public JwtTokenProvider(@Value("${jwt.secret}") String secretKeyBase64,
                            @Value("${jwt.expiration}") long jwtExpirationMs) {
        byte[] decoded = Base64.getDecoder().decode(secretKeyBase64);
        this.key = Keys.hmacShaKeyFor(decoded);
        this.jwtExpirationMs = jwtExpirationMs;
    }

    public String createToken(String userId, String nickname) {
        Map<String,Object> claims = new HashMap<>();
        claims.put("nickname", nickname);

        Date now = new Date();
        Date exp = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                   .setClaims(claims)
                   .setSubject(userId)
                   .setIssuedAt(now)
                   .setExpiration(exp)
                   .signWith(key, SignatureAlgorithm.HS512)
                   .compact();
    }


    /**
     * ✅ JWT 에서 사용자 ID(subject) 추출
     */
    public String getUserIdFromToken(String token) {
        return Jwts.parserBuilder()
                   .setSigningKey(key)
                   .build()
                   .parseClaimsJws(token)
                   .getBody()
                   .getSubject();
    }

    /**
     * ✅ 토큰 유효성 검사
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
