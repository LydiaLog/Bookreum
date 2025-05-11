package com.bookreum.domain.post.controller;

import com.bookreum.domain.post.dto.PostDto;
import com.bookreum.domain.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeController {

    private final PostService postService;

    @GetMapping
    public ResponseEntity<List<PostDto.SimpleResponse>> getHomeData(
            @RequestParam(name = "sort", defaultValue = "latest") String sort,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        List<PostDto.Response> posts;

        if (keyword != null && !keyword.isEmpty()) {
            posts = postService.searchPosts(keyword, sort, pageable);
        } else if (sort.equalsIgnoreCase("latest")) {
            posts = postService.getLatestPosts(pageable);
        } else {
            posts = postService.getOldestPosts(pageable);
        }

        // 프론트에서 필요한 데이터 형식에 맞춰 가공
        List<PostDto.SimpleResponse> simplePosts = posts.stream().map(post -> PostDto.SimpleResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent().length() > 140 ? post.getContent().substring(0, 140) + "…" : post.getContent())
                .date(post.getCreatedAt()) // PostDto.Response에 createdAt 추가해야 함
                .bookTitle(post.getBookTitle())
                .bookAuthor(post.getBookAuthor())
                .coverUrl(post.getBookCoverImageUrl())
                .nickname(post.getAuthorName())
                .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(simplePosts);
    }

    @GetMapping("/write")
    public ResponseEntity<String> getWritePage() {
        return ResponseEntity.ok("Redirect to Write Page");
    }
}
