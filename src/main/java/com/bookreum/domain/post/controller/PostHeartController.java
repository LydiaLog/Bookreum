package com.bookreum.domain.post.controller;

import com.bookreum.domain.post.service.PostHeartService;
import com.bookreum.domain.post.service.PostService;
import com.bookreum.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostHeartController {

    private final PostHeartService postHeartService;
    private final PostService postService; // ✅ PostService 추가

    // ✅ toggle 방식 공감 (로그인 사용자 없이 임시 유저 사용)
    @PostMapping("/{id}/heart")
    public String toggleHeart(@PathVariable Integer id) {
        // ✅ 임시 사용자 (로그인 연동 전)
        User user = User.builder().id(1).nickname("테스터").build();

        boolean added = postHeartService.toggleHeart(id, user);
        return added ? "공감 추가됨" : "공감 취소됨";
    }

    // ✅ 내 글일 때만 공감 수 조회 가능
    @GetMapping("/{id}/heart-count")
    public ResponseEntity<Long> countHearts(@PathVariable Integer id) {
        // ✅ 임시 사용자 (로그인 연동 전)
        User user = User.builder().id(1).nickname("테스터").build();
        
        // ✅ 게시글의 작성자가 현재 사용자와 동일한지 확인
        boolean isMyPost = postService.isMyPost(id, user);
        if (!isMyPost) {
            return ResponseEntity.status(403).body(null); // 권한 없음 (Forbidden)
        }

        long heartCount = postHeartService.countHearts(id);
        return ResponseEntity.ok(heartCount);
    }
}
