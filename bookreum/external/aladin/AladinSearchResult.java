package com.bookreum.external.aladin;

import lombok.Data;
import java.util.List;

/*DTO
 * 알라딘 ItemSearch api 최상위 JSON 구조 매핑
 * {
 *   "totalResults": 123,
 *   "item": [ { ... }, { ... } ]
 * }
 * */

@Data
public class AladinSearchResult {
	private int totalResults; // 검색 결과 총 건수
	private List<AladinItem> item; // 실제 책 정보 리스트
}
