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
        String method = request.getMethod();
        log.info("Processing request for path: {} {}", method, path);

     // (추가) 클럽 조회용 GET 요청은 토큰 검사 없이 통과
        if ("GET".equalsIgnoreCase(method) && path.startsWith("/api/clubs")) {
            log.info("Public GET /api/clubs/** path, skipping token validation");
            filterChain.doFilter(request, response);
            return;
        }
        
        // 1) 공개 API 경로는 토큰 검사 없이 바로 통과
        if (path.startsWith("/api/auth/")
            || path.equals("/api/home")
            || path.startsWith("/api/aladin/search")
            || path.startsWith("/api/books/search")
            || path.startsWith("/api/posts/saveBook")
            || path.startsWith("/api/clubs/saveBook")
            || path.startsWith("/api/clubs/searchBooks")
            || path.startsWith("/api/clubs/public/")  
            || path.equals("/api/clubs/public")       
            || (method.equals("GET") && (
            	path.equals("/api/clubs") ||
                path.startsWith("/api/clubs/") ||
                path.matches("/api/clubs/\\d+") ||
                path.startsWith("/api/posts/") ||
                path.startsWith("/api/books/") ||
                path.startsWith("/api/posts/comments/")
            ))
        ) {
            log.info("Public API path, skipping token validation");
            filterChain.doFilter(request, response);
            return;
        }

        // 2) 그 외 요청은 헤더에서 Bearer 토큰을 추출하여 유효성 검사
        String header = request.getHeader("Authorization");
        log.info("Authorization header: {}", header);
        
        if (!StringUtils.hasText(header) || !header.startsWith("Bearer ")) {
            log.warn("No valid Authorization header found");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("No token provided");
            return;
        }

        String token = header.substring(7);
        log.info("Extracted token: {}", token);
        
        if (!jwtTokenProvider.validateToken(token)) {
            log.warn("Token validation failed");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid token");
            return;
        }

        try {
            String kakaoId = jwtTokenProvider.getKakaoIdFromToken(token);
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
            
            // 인증 성공 시 다음 필터로 진행
            filterChain.doFilter(request, response);
        } catch (UsernameNotFoundException e) {
            log.warn("JWT 토큰의 subject에 해당하는 유저가 없습니다: {}", e.getMessage());
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("User not found");
        } catch (Exception e) {
            log.error("JWT 토큰 처리 중 오류 발생: {}", e.getMessage(), e);
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token processing error");
        }
    }
}