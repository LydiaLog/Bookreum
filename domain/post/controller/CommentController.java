package com.bookreum.domain.post.controller;

import com.bookreum.domain.post.dto.CommentDto;
import com.bookreum.domain.post.entity.Comment;
import com.bookreum.domain.post.service.CommentService;
import com.bookreum.domain.user.entity.User;
import com.bookreum.domain.post.entity.Post;
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
        // 임시 유저/포스트 (나중에 실제 인증/조회 연결)
        User user = User.builder().id(1L).nickname("테스터").build();
        Post post = Post.builder().id(request.getPostId()).build();

        return commentService.createComment(request, user, post);
    }

    // 특정 게시글의 댓글 전체 조회
    @GetMapping("/post/{postId}")
    public List<CommentDto.Response> getCommentsByPost(@PathVariable Long postId) {
        return commentService.getCommentsByPost(postId);
    }

    // 댓글 삭제
    @DeleteMapping("/{id}")
    public void deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
    }
    
    @PutMapping("/{id}")
    public void updateComment(@PathVariable Long id, @RequestBody CommentDto.Request request) {
        commentService.updateComment(id, request.getContent());
    }

}
