package com.bookreum.external.aladin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

/**
 * AladinBookClient 의 실제 구현체.
 *  - Spring WebClient 로 알라딘 ItemSearch API 호출
 *  - 동기(block) 방식으로 AladinSearchResult 반환
 */
@Slf4j
@Component                    // 스프링 빈 등록
@RequiredArgsConstructor
public class AladinBookClientImpl implements AladinBookClient {

    /** 알라딘 TTB API 키 (application.properties aladin.ttb-key) */
    @Value("${aladin.ttb-key}")
    private String ttbKey;

    /** WebClient: baseUrl 고정 */
    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://www.aladin.co.kr/ttb/api")
            .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
            .build();

    /**
     * 키워드로 책 검색(최대 5권).
     * @param keyword 검색 키워드(제목·저자·ISBN 등)
     * @return AladinSearchResult DTO
     */
    @Override
    public AladinSearchResult searchBooks(String keyword) {

        Mono<AladinSearchResult> mono = webClient.get()
                .uri(uriBuilder -> buildItemSearchUri(uriBuilder, keyword))
                .retrieve()
                .bodyToMono(AladinSearchResult.class)
                .doOnError(e -> log.error("Aladin ItemSearch API 호출 실패: {}", e.getMessage()));

        // 입문단계에서는 blocking 사용, 이후 reactive 로 변경 가능
        return mono.block();
    }

    /** URI 조립 전용 메서드 */
    private java.net.URI buildItemSearchUri(UriBuilder uriBuilder, String keyword) {

        return uriBuilder
                .path("/ItemSearch.aspx")
                .queryParam("ttbkey",     ttbKey)          // API 키
                .queryParam("Query",      keyword)         // 검색어
                .queryParam("QueryType",  "Keyword")       // 제목·저자·ISBN 혼합
                .queryParam("SearchTarget", "Book")        // 도서만
                .queryParam("Output",     "JS")            // JSON 반환
                .queryParam("Version",    "20131101")      // 최신 버전
                .queryParam("Cover",      "Mid")           // 표지 크기
                .queryParam("MaxResults", 5)               // 최대 5권
                .build();
    }
}
