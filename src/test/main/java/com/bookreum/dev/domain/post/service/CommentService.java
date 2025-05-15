package com.bookreum.dev.domain.post.service;

import com.bookreum.dev.domain.post.dto.CommentDto;
import com.bookreum.dev.domain.post.entity.CommentEntity;
import com.bookreum.dev.domain.post.entity.PostEntity;
import com.bookreum.dev.domain.post.repository.CommentRepository;
import com.bookreum.dev.domain.post.repository.CommentHeartRepository;
import com.bookreum.dev.domain.user.UserEntity;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentHeartRepository commentHeartRepository;

    @Transactional
    public CommentDto.Response createComment(CommentDto.Request request, UserEntity user, PostEntity post) {
        // 빌더 패턴 사용으로 Comment 생성
        CommentEntity comment = CommentEntity.builder()
                .content(request.getContent())
                .user(user)
                .post(post)
                .build();

        CommentEntity saved = commentRepository.save(comment);
        return CommentDto.Response.fromEntity(saved);
    }

    // 댓글 조회 (소유자 여부에 따른 통계 노출)
    // CommentService에서 댓글 조회 로직
 // CommentService
    public List<CommentDto.Response> getCommentsByPost(Integer postId, boolean isOwner) {
        return commentRepository.findByPostId(postId).stream()
                .map(comment -> {
                    String nickname = comment.getUser() != null ? comment.getUser().getNickname() : "Unknown";
                    System.out.println("📌 댓글 작성자 닉네임: " + nickname);

                    long heartCount = isOwner ? commentHeartRepository.countByComment(comment) : 0;
                    return CommentDto.Response.fromEntityWithHeartCount(comment, heartCount);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteComment(Integer id) {
        commentRepository.deleteById(id);
    }

    @Transactional
    public void updateComment(Integer commentId, String newContent) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));
        
        // Setter로 내용 업데이트
        comment.setContent(newContent);
        commentRepository.save(comment); // 변경 내용 저장
    }
}