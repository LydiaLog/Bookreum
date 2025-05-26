package com.bookreum.dev.domain.book;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import java.time.Duration;
import reactor.util.retry.Retry;

@Slf4j
@Component
@RequiredArgsConstructor
public class AladinBookClientImpl implements AladinBookClient {

    @Value("${aladin.ttb-key}")
    private String ttbKey; // properties 파일에서 API 키 로드

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://www.aladin.co.kr/ttb/api")
            .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
            .build();

    @Override
    public AladinSearchResult searchBooks(String keyword) {
        try {
            log.info("알라딘 API 호출 시작 - 키워드: {}", keyword);
            
            return webClient.get()
            	    .uri(uriBuilder -> buildItemSearchUri(uriBuilder, keyword))
            	    .retrieve()
            	    .onStatus(status -> !status.is2xxSuccessful(),
            	        clientResponse -> {
            	            log.error("알라딘 API HTTP 에러 - 상태코드: {}", clientResponse.statusCode());
            	            return clientResponse.bodyToMono(String.class)
            	                                 .map(errorBody -> new RuntimeException("API Error: " + errorBody));
            	        })
            	    .bodyToMono(AladinSearchResult.class)
            	    // 응답이 없으면 빈 객체로 대체
            	    .defaultIfEmpty(new AladinSearchResult())
            	    // totalResults 가 null 이면 0으로 세팅
            	    .map(result -> {
            	        if (result.getTotalResults() == null) {
            	            result.setTotalResults(0);
            	        }
            	        return result;
            	    })
            	    .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
            	        .filter(throwable -> throwable instanceof reactor.netty.http.client.PrematureCloseException)
            	        .doBeforeRetry(retrySignal ->
            	            log.warn("알라딘 API 재시도 중... 시도 횟수: {}", retrySignal.totalRetries() + 1)))
            	    .timeout(Duration.ofSeconds(15))
            	    .block();

        } catch (Exception e) {
            log.error("Aladin API 호출 중 오류 발생 - 키워드: {}, 에러: {}", keyword, e.getMessage(), e);
            throw new RuntimeException("책 정보를 가져오는 데 실패했습니다. 나중에 다시 시도해주세요.");
        }
    }

    private java.net.URI buildItemSearchUri(UriBuilder uriBuilder, String keyword) {
        return uriBuilder
                .path("/ItemSearch.aspx")
                .queryParam("ttbkey", ttbKey)
                .queryParam("Query", keyword)
                .queryParam("QueryType", "Keyword")
                .queryParam("SearchTarget", "Book")
                .queryParam("Output", "js")
                .queryParam("Version", "20131101")
                .queryParam("Cover", "Mid")
                .queryParam("MaxResults", 10)
                .build();
    }
}