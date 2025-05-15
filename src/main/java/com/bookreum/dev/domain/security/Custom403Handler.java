package com.bookreum.dev.domain.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * 🔒 403 Forbidden 발생 시 JSON 응답을 내려주는 커스텀 핸들러
 */
@Slf4j
@Component
public class Custom403Handler implements AccessDeniedHandler {

    private static final DateTimeFormatter ISO =
        DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneId.of("Asia/Seoul"));

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException ex
    ) throws IOException, ServletException {

        /* 1️⃣ 서버 로그 기록 */
        log.warn("🚫 403 Forbidden ▶ method={} uri={} ip={}",
                 request.getMethod(),
                 request.getRequestURI(),
                 request.getRemoteAddr());

        /* 2️⃣ JSON 형태 응답 */
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");

        String body = String.format(
            "{ \"timestamp\":\"%s\", \"status\":403, \"error\":\"FORBIDDEN\", " +
            "\"message\":\"접근이 거부되었습니다. 권한을 확인하세요.\", \"path\":\"%s\" }",
            ISO.format(Instant.now()),
            request.getRequestURI()
        );
        response.getWriter().write(body);
    }
}
