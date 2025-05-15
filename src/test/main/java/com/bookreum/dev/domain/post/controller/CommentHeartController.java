package com.bookreum.dev.domain.post.controller;

import com.bookreum.dev.domain.post.service.CommentHeartService;
import com.bookreum.dev.domain.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentHeartController {

    private final CommentHeartService commentHeartService;

    // 1. 댓글에 공감 토글 (누르면 추가, 다시 누르면 삭제)
    @PostMapping("/{commentId}/hearts")
    public void toggleHeart(@PathVariable Integer commentId) {
        // TODO: 인증 붙이면 user 추출해서 교체
        UserEntity user = UserEntity.builder().id(1).nickname("테스터").build();
        commentHeartService.toggleHeart(commentId, user);
    }

    // 2. 댓글 공감 여부 확인
    @GetMapping("/{commentId}/hearts/exists")
    public boolean hasHeart(@PathVariable Integer commentId) {
        // TODO: 인증 붙이면 user 추출해서 교체
        UserEntity user = UserEntity.builder().id(1).nickname("테스터").build();
        return commentHeartService.hasHeart(commentId, user);
    }

    // 3. 댓글 공감 수 조회
    @GetMapping("/{commentId}/hearts/count")
    public Long getHeartCount(@PathVariable Integer commentId) {
        return commentHeartService.getHeartCount(commentId);
    }
}
