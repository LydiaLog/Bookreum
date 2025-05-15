package com.bookreum.dev.aicontent;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommend")
@RequiredArgsConstructor
public class ContentBasedController {

    private final ContentBasedService contentBasedService;

    // 1. 단순 제목 기반 추천
    //http://localhost:8080/api/recommend/title?title=채식주의자
    //→ 알라딘 검색 기반 추천
    @GetMapping("/title")
    public List<ContentBookDto> recommendByTitle(@RequestParam String title) {
        return contentBasedService.recommendByTitle(title);
    }

    // 2. 콘텐츠 기반 AI 추천
    //http://localhost:8080/api/recommend/content?isbn13=9788956055466
    //→ 콘텐츠 기반 AI 추천 (파이썬 연동)
    @GetMapping("/content")
    public List<ContentBookDto> recommendByIsbn13(@RequestParam String isbn13) {
        return contentBasedService.recommendContentBased(isbn13);
    }
}