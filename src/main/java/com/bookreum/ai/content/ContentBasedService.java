package com.bookreum.ai.content;

import com.bookreum.external.aladin.AladinBookClient;
import com.bookreum.external.aladin.AladinItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContentBasedService {

    private final AladinBookClient aladinBookClient;

    // 1. 알라딘 API로 단순 제목 기반 검색 추천
    //"죽고 싶지만 떡볶이는 먹고 싶어"를 검색하면
    //알라딘이 "죽고 싶지만" 이나 "떡볶이" 같은 키워드가 포함된 책들을 보여줌.
    public List<ContentBookDto> recommendByTitle(String title) {
        return aladinBookClient.searchBooks(title).getItem()
        		.stream()
                .limit(5)
                .map(this::mapToDto)
                .toList();
    }

    // 2. 콘텐츠 기반 AI 추천 (Python 연동)
    //"죽고 싶지만 떡볶이는 먹고 싶어" → 내용이 비슷한 감정 에세이/우울증 관련 회복 책 등 
    //의미적으로 유사한 책 추천 가능.
    public List<ContentBookDto> recommendContentBased(String isbn13) {
        List<String> recommendedIsbn13s = runPythonRecommendation(isbn13);
        List<ContentBookDto> result = new ArrayList<>();

        for (String isbn : recommendedIsbn13s) {
            List<AladinItem> items = aladinBookClient.searchBooks(isbn).getItem();
            if (!items.isEmpty()) {
                result.add(mapToDto(items.get(0)));  // 첫 번째 결과만 사용
            }
        }

        return result;
    }

    // 3. 파이썬 추천기 실행 함수
    private List<String> runPythonRecommendation(String isbn13) {
        List<String> result = new ArrayList<>();
        try {
            ProcessBuilder pb = new ProcessBuilder("python", "recommender/run_model.py", isbn13);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    result.add(line.trim());
                }
            }

            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
 // AladinItem → DTO 변환
    private ContentBookDto mapToDto(AladinItem item) {
        return ContentBookDto.builder()
                .isbn13(item.getIsbn13())
                .title(item.getTitle())
                .author(item.getAuthor())
                .coverImageUrl(item.getCover())
                .build();
    }
}
