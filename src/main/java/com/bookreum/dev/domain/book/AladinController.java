package com.bookreum.dev.domain.book;

import com.bookreum.dev.domain.book.AladinSearchResult;
import com.bookreum.dev.domain.book.AladinBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/aladin")
@CrossOrigin(origins = "http://10.50.216.86:5173")
@RequiredArgsConstructor
public class AladinController {

    private final AladinBookService aladinBookService;

    @GetMapping("/search")
    public AladinSearchResult searchBooks(@RequestParam("keyword") String keyword) {
        return aladinBookService.searchBooks(keyword);
    }
}