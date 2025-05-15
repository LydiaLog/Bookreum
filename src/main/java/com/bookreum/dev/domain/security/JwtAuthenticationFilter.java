package com.bookreum.dev.domain.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 모든 요청마다 한 번씩 실행되며,
 * Authorization 헤더의 Bearer 토큰을 검증하고
 * 유효한 경우 스프링 시큐리티 컨텍스트에 Authentication을 등록합니다.
 */
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /** JWT 생성·검증 로직을 담당 */
    private final JwtTokenProvider tokenProvider;
    /** 토큰으로부터 로드한 사용자 정보를 기반으로 권한을 설정할 UserDetailsService */
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws ServletException, IOException {
        // 1) 요청 헤더에서 토큰 추출
        String token = resolveToken(request);

        // 2) 토큰이 존재하고 유효하면
        if (token != null && tokenProvider.validateToken(token)) {
            // 2.1) 토큰에서 카카오ID(subject) 추출
            String kakaoId = tokenProvider.getKakaoIdFromToken(token);

            // 2.2) UserDetailsService를 통해 DB에서 사용자 정보 로드
            UserDetails userDetails = userDetailsService.loadUserByUsername(kakaoId);

            // 2.3) 인증 객체 생성 (권한 포함)
            UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
                );
            // 2.4) 요청 세부정보 설정 (IP, 세션 등)
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // 2.5) 시큐리티 컨텍스트에 인증 정보 저장
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        // 다음 필터 체인으로 계속 진행
        chain.doFilter(request, response);
    }

    /**
     * Authorization 헤더에서 "Bearer " 접두사를 제거하고 순수 토큰을 반환합니다.
     * @param request 현재 HTTP 요청
     * @return 순수 JWT 토큰 또는 null
     */
    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }

    /**
     * 인증이 필요 없는 경로(예: 로그인, 인증 콜백, WebSocket 핸드셰이크 엔드포인트 등)를 필터에서 제외할 수 있습니다.
     * 필요에 따라 여기에 URI 패턴을 추가하세요.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/api/auth")
            || path.startsWith("/ws-chat");
    }
}
