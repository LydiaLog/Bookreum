package com.bookreum.dev.domain.book;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AladinItem {
	private Integer   itemId;        // 알라딘 내부 식별자
    private String title;         // 도서 제목
    private String author;        // 저자명
    private String cover;         // 표지 이미지 URL
    private String isbn13;        // ISBN-13
    private String categoryName;  // 카테고리 이름
    private String description;   // 간략 소개
}