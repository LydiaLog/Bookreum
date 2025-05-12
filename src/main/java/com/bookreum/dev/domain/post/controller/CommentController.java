package com.bookreum.dev.domain.post.controller;

import com.bookreum.dev.domain.post.dto.CommentDto;
import com.bookreum.dev.domain.post.entity.CommentEntity;
import com.bookreum.dev.domain.post.service.CommentService;
import com.bookreum.dev.domain.user.UserEntity;
import com.bookreum.dev.domain.post.entity.PostEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 댓글 생성
    @PostMapping
    public CommentDto.Response createComment(@RequestBody CommentDto.Request request) {
        UserEntity user = UserEntity.builder().id(1).nickname("테스터").build();
        PostEntity post = PostEntity.builder().id(request.getPostId()).build();
        return commentService.createComment(request, user, post);
    }

    // 특정 게시글의 댓글 전체 조회 (본인 여부 확인)
    @GetMapping("/post/{postId}")
    public List<CommentDto.Response> getCommentsByPost(@PathVariable Integer postId, @RequestParam boolean isOwner) {
        return commentService.getCommentsByPost(postId, isOwner);
    }

    // 댓글 삭제
    @DeleteMapping("/{id}")
    public void deleteComment(@PathVariable Integer id) {
        commentService.deleteComment(id);
    }

    // 댓글 수정
    @PutMapping("/{id}")
    public void updateComment(@PathVariable Integer id, @RequestBody CommentDto.Request request) {
        commentService.updateComment(id, request.getContent());
    }
}