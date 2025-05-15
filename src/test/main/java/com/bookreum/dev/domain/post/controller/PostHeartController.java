package com.bookreum.dev.domain.post.controller;

import com.bookreum.dev.domain.post.service.PostHeartService;
import com.bookreum.dev.domain.post.service.PostService;
import com.bookreum.dev.domain.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostHeartController {

    private final PostHeartService postHeartService;
    private final PostService postService;

    @PostMapping("/{id}/heart")
    public ResponseEntity<String> toggleHeart(@PathVariable("id") Integer id) {
        UserEntity user = UserEntity.builder().id(1).nickname("테스터").build();
        boolean added = postHeartService.toggleHeart(id, user);
        String message = added ? "공감 추가됨" : "공감 취소됨";
        return ResponseEntity.ok(message);
    }

    @GetMapping("/{id}/heart")
    public ResponseEntity<Boolean> isHearted(@PathVariable("id") Integer id) {
        UserEntity user = UserEntity.builder().id(1).nickname("테스터").build();
        boolean isHearted = postHeartService.isHearted(id, user);
        return ResponseEntity.ok(isHearted);
    }

    @GetMapping("/{id}/heart-count")
    public ResponseEntity<Long> countHearts(@PathVariable("id") Integer id) {
        UserEntity user = UserEntity.builder().id(1).nickname("테스터").build();
        boolean isMyPost = postService.isMyPost(id, user);
        if (!isMyPost) {
            return ResponseEntity.status(403).body(null);
        }

        long heartCount = postHeartService.countHearts(id);
        return ResponseEntity.ok(heartCount);
    }
}
