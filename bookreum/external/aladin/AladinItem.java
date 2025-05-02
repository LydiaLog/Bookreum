package com.bookreum.external.aladin;

import lombok.Data;

/* DTO
 * 알라딘 검색 api가 반환하는 item 배열의 각 요소 정보
 * 각 요소는 한 권의 책의 정보를 담고 있음
 * JSON 필드 명과 동일하게 선언해야 Jackson이 자동 매핑 됨.
 */

@Data
public class AladinItem {
	private Long itemId; //고유 상품 id
	private String title; // 책 제목
	private String author; // 저자명 (여러명이면 콤마로 구분)
	private String cover; // 표지 이미지 url (기본 mid 사이즈)
	private String isbn13;
	private String categoryName; // 알라딘 카테고리명
}
