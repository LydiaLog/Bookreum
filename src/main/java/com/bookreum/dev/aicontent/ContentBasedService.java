package com.bookreum.dev.aicontent;

import com.bookreum.dev.domain.book.AladinBookClient;
import com.bookreum.dev.domain.book.AladinItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * AI 콘텐츠 기반 추천을 담당하는 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ContentBasedService {

    private final AladinBookClient aladinBookClient;

    // Python 실행 커맨드 (application.properties 에서 주입)
    @Value("${recommend.python.exec}")
    private String pythonExec;

    // 추천 스크립트 경로
    @Value("${recommend.python.script}")
    private String pythonScript;

    // 스크립트 타임아웃(초)
    @Value("${recommend.python.timeout}")
    private long pythonTimeout;

    /** 1) 단순 제목 검색 기반 추천 */
    public List<ContentBookDto> recommendByTitle(String title) {
        return aladinBookClient.searchBooks(title).getItem().stream()
                .limit(5)
                .map(this::mapToDto)
                .toList();
    }

    /** 2) AI 콘텐츠 기반 추천 */
    public List<ContentBookDto> recommendContentBased(String isbn13) {
        List<String> recIsbns = runPythonRecommendation(isbn13);

        if (recIsbns.isEmpty()) {
            log.warn("AI 추천 결과 없음 → 제목 기반 fallback");
            return recommendByTitle(isbn13);
        }

        return recIsbns.stream()
                .map(isbn -> {
                    try {
                        // ISBN 정확히 매칭된 첫 결과만 사용
                        return aladinBookClient.searchBooks(isbn).getItem().stream()
                                .filter(item -> isbn.equals(item.getIsbn13()))
                                .findFirst()
                                .map(this::mapToDto)
                                .orElse(null);
                    } catch (Exception e) {
                        log.error("알라딘 검색 실패 isbn={}", isbn, e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }

    /** 3) 외부 Python 스크립트 호출, 결과로 ISBN 리스트 반환 */
    private List<String> runPythonRecommendation(String isbn13) {
        List<String> result = new ArrayList<>();
        ProcessBuilder pb = new ProcessBuilder(pythonExec, pythonScript, isbn13);
        pb.redirectErrorStream(true);

        try {
            Process proc = pb.start();

            // 지정된 시간 안에 완료되지 않으면 강제 종료
            if (!proc.waitFor(pythonTimeout, TimeUnit.SECONDS)) {
                proc.destroyForcibly();
                throw new RuntimeException("추천 엔진 응답 시간 초과");
            }

            // stdout 에서 한 줄씩 ISBN 읽기
            try (BufferedReader reader = new BufferedReader(
                     new InputStreamReader(proc.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    result.add(line.trim());
                }
            }

            // 스크립트의 종료 코드 확인
            int exitCode = proc.exitValue();
            if (exitCode != 0) {
                throw new RuntimeException("추천 엔진 비정상 종료 (exit=" + exitCode + ")");
            }

        } catch (IOException | InterruptedException e) {
            log.error("추천 엔진 실행 오류", e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "추천 서비스 오류: " + e.getMessage()
            );
        }

        return result;
    }

    /** 알라딘 API DTO → 내부 DTO 매핑 */
    private ContentBookDto mapToDto(AladinItem item) {
        return ContentBookDto.builder()
                .isbn13(item.getIsbn13())
                .title(item.getTitle())
                .author(item.getAuthor())
                .coverImageUrl(item.getCover())
                .build();
    }
}
