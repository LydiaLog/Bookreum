package com.bookreum.dev.domain.book;

import lombok.Data;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AladinSearchResult {
	/** 전체 검색 결과 건수 */
    private Integer totalResults;
    /** 실제 반환된 도서 리스트 (최대 MaxResults 개) */
    private List<AladinItem> item;

}