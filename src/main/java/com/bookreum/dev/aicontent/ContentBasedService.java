package com.bookreum.dev.aicontent;

import com.bookreum.dev.domain.book.AladinBookClient;
import com.bookreum.dev.domain.book.AladinItem;

import io.jsonwebtoken.io.IOException;
import io.jsonwebtoken.lang.Objects;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContentBasedService {

    private final AladinBookClient aladinBookClient;

    @Value("${recommend.python.exec}")
    private String pythonExec;

    @Value("${recommend.python.script}")
    private String pythonScript;

    @Value("${recommend.python.timeout}")
    private long pythonTimeout;

    /** 1) 단순 제목 검색 추천 */
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
            // fallback: 제목 키워드 검색
            log.warn("AI 추천 결과 없음 → 제목 기반 fallback");
            return recommendByTitle(isbn13);
        }

        return recIsbns.stream()
                .map(isbn -> {
                    try {
                        // 정확한 ISBN 검색
                        return aladinBookClient.searchBooks(isbn).getItem().stream()
                                .filter(item -> isbn.equals(item.getIsbn13()))
                                .findFirst()
                                .map(this::mapToDto)
                                .orElse(null);
                    } catch (Exception e) {
                        log.error("Aladin 검색 실패 isbn={}", isbn, e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }

    /** 3) 외부 Python 스크립트 안전 호출 */
    private List<String> runPythonRecommendation(String isbn13) {
        List<String> result = new ArrayList<>();
        ProcessBuilder pb = new ProcessBuilder(pythonExec, pythonScript, isbn13);

        // (필요 시) 스크립트 위치를 별도 디렉터리로 지정
        // pb.directory(new File("/opt/app/recommender"));

        pb.redirectErrorStream(true);

        try {
            Process proc = pb.start();

            // 타임아웃을 주고, 경과 시 파괴
            if (!proc.waitFor(pythonTimeout, TimeUnit.SECONDS)) {
                proc.destroyForcibly();
                throw new RuntimeException("추천 엔진 응답 시간 초과");
            }

            try (BufferedReader reader = new BufferedReader(
                      new InputStreamReader(proc.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    result.add(line.trim());
                }
            }

            int exit = proc.exitValue();
            if (exit != 0) {
                throw new RuntimeException("추천 엔진 비정상 종료, exit=" + exit);
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

    /** AladinItem → DTO */
    private ContentBookDto mapToDto(AladinItem item) {
        return ContentBookDto.builder()
                .isbn13(item.getIsbn13())
                .title(item.getTitle())
                .author(item.getAuthor())
                .coverImageUrl(item.getCover())
                .build();
    }
}
