package com.bookreum.dev.domain.security;

import io.jsonwebtoken.*;                             // JWT 생성·파싱 라이브러리
import io.jsonwebtoken.security.Keys;                 // HMAC 키 생성 유틸
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;                              // 서명 키 인터페이스
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 토큰 생성·검증을 담당하는 컴포넌트
 */
@Component
public class JwtTokenProvider {

    /** 서명에 사용할 대칭 키 */
    private final Key key;

    /** 토큰 만료 시간(밀리초) */
    private final long jwtExpirationMs;

    /**
     * 생성자: application.properties 에 정의된 값을 주입받아 키와 만료 시간을 초기화
     *
     * @param secretKeyBase64 Base64로 인코딩된 비밀키 문자열
     * @param jwtExpirationMs 토큰 만료 시간(밀리초)
     */
    public JwtTokenProvider(
            @Value("${jwt.secret}") String secretKeyBase64,
            @Value("${jwt.expiration}") long jwtExpirationMs) {

        // 1) Base64 문자열 디코딩
        byte[] decoded = Base64.getDecoder().decode(secretKeyBase64);

        // 2) HMAC-SHA 키 생성 (256비트 이상 권장)
        this.key = Keys.hmacShaKeyFor(decoded);

        // 3) 만료 시간 저장
        this.jwtExpirationMs = jwtExpirationMs;
    }

    /**
     * JWT 액세스 토큰을 생성합니다.
     *
     * @param kakaoId       토큰의 subject 으로 들어갈 카카오 고유 ID
     * @param nickname      custom 클레임으로 넣을 사용자 닉네임
     * @param profileImage  custom 클레임으로 넣을 프로필 이미지 URL
     * @return               서명된 JWT 토큰 문자열 (compact 형식)
     */
    public String createToken(String kakaoId, String nickname, String profileImage) {
        // 1) custom 클레임 설정
        Map<String, Object> claims = new HashMap<>();
        claims.put("nickname", nickname);
        claims.put("profileImage", profileImage);

        // 2) 발급 시각 및 만료 시각 계산
        Date now = new Date();
        Date exp = new Date(now.getTime() + jwtExpirationMs);

        // 3) 토큰 빌드 및 서명 → compact 문자열 반환
        return Jwts.builder()
                   .setClaims(claims)           // 커스텀 클레임
                   .setSubject(kakaoId)          // 표준 클레임: subject
                   .setIssuedAt(now)             // 표준 클레임: iat
                   .setExpiration(exp)           // 표준 클레임: exp
                   .signWith(key, SignatureAlgorithm.HS512) // HS512로 서명
                   .compact();
    }

    /**
     * JWT에서 subject(카카오ID)를 추출합니다.
     *
     * @param token 서명된 JWT 토큰
     * @return      토큰에 담긴 subject 값 (카카오ID)
     * @throws JwtException 토큰 파싱에 실패하면 예외 발생
     */
    public String getKakaoIdFromToken(String token) {
        return Jwts.parserBuilder()
                   .setSigningKey(key)        // 검증에 사용할 키
                   .build()
                   .parseClaimsJws(token)     // 서명 및 구조 검증
                   .getBody()                 // 클레임(페이로드) 얻기
                   .getSubject();             // subject 반환
    }

    /**
     * JWT가 유효한지 검증합니다.
     *
     * @param token 서명된 JWT 토큰
     * @return      유효하면 true, 아니면 false
     */
    public boolean validateToken(String token) {
        try {
            // 구조, 서명, 만료시간 모두 검증
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // 잘못된 토큰, 만료된 토큰, 서명 불일치 등 모든 경우 false 반환
            return false;
        }
    }
}
