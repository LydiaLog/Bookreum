package com.bookreum.dev.domain.book;

import com.bookreum.dev.domain.book.AladinSearchResult;
import com.bookreum.dev.domain.book.AladinBookService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/aladin")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AladinController {

    private final AladinBookService aladinBookService;

    /**
     * GET /api/aladin/search?keyword=키워드
     * @return 검색된 책 목록과 총 개수
     */
    @GetMapping("/search")
    public AladinSearchResult search(@RequestParam("keyword") String kw) {
        AladinSearchResult result = aladinBookService.searchBooks(kw);
        if (result == null || result.getTotalResults() == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "검색 결과가 없습니다.");
        }
        return result;
    }
    /**
     * 단순히 책 목록만 뽑아 보내고 싶다면
     */
    @GetMapping("/search/items")
    public List<AladinItem> items(@RequestParam("keyword") String kw) {
        AladinSearchResult r = aladinBookService.searchBooks(kw);
        return r == null ? List.of() : r.getItem();
    }
}