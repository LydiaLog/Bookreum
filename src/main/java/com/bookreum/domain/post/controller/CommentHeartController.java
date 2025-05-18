package com.bookreum.domain.post.controller;

import com.bookreum.domain.post.service.CommentHeartService;
import com.bookreum.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentHeartController {

    private final CommentHeartService commentHeartService;

    // 1. 댓글에 공감 토글 (누르면 추가, 다시 누르면 삭제)
    @PostMapping("/{commentId}/hearts")
    public ResponseEntity<Void> toggleHeart(@PathVariable("commentId") Integer commentId) {
        // TODO: 인증 붙이면 user 추출해서 교체
        User user = User.builder().id(1).nickname("테스터").build();
        commentHeartService.toggleHeart(commentId, user);
        return ResponseEntity.ok().build();
    }

    // 2. 댓글 공감 여부 확인
    @GetMapping("/{commentId}/hearts/exists")
    public ResponseEntity<Boolean> hasHeart(@PathVariable("commentId") Integer commentId) {
        // TODO: 인증 붙이면 user 추출해서 교체
        User user = User.builder().id(1).nickname("테스터").build();
        boolean exists = commentHeartService.hasHeart(commentId, user);
        return ResponseEntity.ok(exists);
    }

    // 3. 댓글 공감 수 조회
    @GetMapping("/{commentId}/hearts/count")
    public ResponseEntity<Long> getHeartCount(@PathVariable("commentId") Integer commentId) {
        Long heartCount = commentHeartService.getHeartCount(commentId);
        return ResponseEntity.ok(heartCount);
    }
}
