package com.bookreum.dev.domain.post.controller;

import com.bookreum.dev.domain.post.dto.CommentDto;
import com.bookreum.dev.domain.post.entity.PostEntity;
import com.bookreum.dev.domain.post.repository.PostRepository;
import com.bookreum.dev.domain.post.service.CommentService;
import com.bookreum.dev.domain.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final PostRepository postRepository;

    /**
     * 댓글 생성
     */
    @PostMapping
    public ResponseEntity<CommentDto.Response> createComment(
            @RequestBody CommentDto.Request request
    ) {
        // TODO: 실제 인증된 사용자 정보로 대체
        UserEntity user = UserEntity.builder()
                .id(1)
                .nickname("테스터")
                .build();

        // PostEntity 로드
        PostEntity post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "게시글을 찾을 수 없습니다. id=" + request.getPostId()
                ));

        // 댓글 생성
        CommentDto.Response response = commentService.createComment(request, user, post);
        return ResponseEntity.ok(response);
    }

    /**
     * 특정 게시글의 댓글 전체 조회
     */
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentDto.Response>> getCommentsByPost(
            @PathVariable Integer postId,
            @RequestParam boolean isOwner
    ) {
        List<CommentDto.Response> comments = commentService.getCommentsByPost(postId, isOwner);
        return ResponseEntity.ok(comments);
    }

    /**
     * 댓글 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Integer id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 댓글 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateComment(
            @PathVariable Integer id,
            @RequestBody CommentDto.Request request
    ) {
        commentService.updateComment(id, request.getContent());
        return ResponseEntity.ok().build();
    }
}
