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

 // 2. 콘텐츠 기반 AI 추천 (Python 연동 - 제목 기반)
    public List<ContentBookDto> recommendContentBased(String title) {
        // 1. Python 추천기에서 제목을 기반으로 유사한 책 추천 받기
        List<String> recommendedBooks = runPythonRecommendation(title);
        List<ContentBookDto> result = new ArrayList<>();

        // 2. 추천된 제목으로 Aladin API에서 책 정보 가져오기
        for (String bookInfo : recommendedBooks) {
            String[] info = bookInfo.split("\\|");
            if (info.length >= 1) {
                ContentBookDto dto = ContentBookDto.builder()
                        .title(info[0].trim())
                        .description(info[1].trim())
                        .build();
                result.add(dto);
            }
        }

        return result;
    }

    // 3. 파이썬 추천기 실행 함수 (제목 기반)
    private List<String> runPythonRecommendation(String title) {
        List<String> result = new ArrayList<>();
        try {
            ProcessBuilder pb = new ProcessBuilder("python", "run_model.py", title);
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