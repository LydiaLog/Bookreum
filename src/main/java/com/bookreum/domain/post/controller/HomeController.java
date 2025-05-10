package com.bookreum.domain.post.controller;

import com.bookreum.domain.post.dto.PostDto;
import com.bookreum.domain.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeController {

    private final PostService postService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getHomeData(
            @RequestParam(defaultValue = "latest") String sort,
            @RequestParam(required = false) String keyword) {

        Map<String, Object> response = new HashMap<>();

        List<PostDto.Response> posts;
        if (keyword != null && !keyword.isEmpty()) {
            posts = postService.searchPosts(keyword, sort, null);
        } else {
            posts = sort.equals("latest") ? postService.getLatestPosts() : postService.getOldestPosts();
        }

        response.put("posts", posts);
        return ResponseEntity.ok(response);
    }
}
