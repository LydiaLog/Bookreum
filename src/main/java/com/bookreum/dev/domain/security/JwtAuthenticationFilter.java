package com.bookreum.dev.domain.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // 1) 공개 API 경로는 토큰 검사 없이 바로 통과
        if (path.startsWith("/api/auth/")
                || path.equals("/api/home")
                || path.equals("/api/aladin/search")
                || path.equals("/api/books/search")
                || path.equals("/api/posts/saveBook")
                || path.equals("/api/clubs/saveBook")
                || path.equals("/api/clubs/searchBooks")) {
                filterChain.doFilter(request, response);
                return;
            }

        // 2) 그 외 요청은 헤더에서 Bearer 토큰을 추출하여 유효성 검사
        String header = request.getHeader("Authorization");
        log.info("Authorization header: {}", header);
        
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            log.info("Extracted token: {}", token);
            
            if (jwtTokenProvider.validateToken(token)) {
                log.info("Token is valid");
                String kakaoId = null;
                try {
                    kakaoId = jwtTokenProvider.getKakaoIdFromToken(token);
                    log.info("Kakao ID from token: {}", kakaoId);
                    
                    UserDetails userDetails = userDetailsService.loadUserByUsername(kakaoId);
                    UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                        );
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    log.info("Authentication set in SecurityContext");
                } catch (UsernameNotFoundException e) {
                    log.warn("JWT 토큰의 subject에 해당하는 유저가 없습니다: {}", kakaoId);
                    SecurityContextHolder.clearContext();
                } catch (Exception e) {
                    log.error("JWT 토큰 처리 중 오류 발생: {}", e.getMessage(), e);
                    SecurityContextHolder.clearContext();
                }
            } else {
                log.warn("Token validation failed");
            }
        } else {
            log.warn("No valid Authorization header found");
        }

        // 3) 다음 필터 실행
        filterChain.doFilter(request, response);
    }
}