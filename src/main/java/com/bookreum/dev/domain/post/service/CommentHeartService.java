package com.bookreum.dev.domain.post.service;

import com.bookreum.dev.domain.post.entity.CommentEntity;
import com.bookreum.dev.domain.post.entity.CommentHeart;
import com.bookreum.dev.domain.post.repository.CommentHeartRepository;
import com.bookreum.dev.domain.post.repository.CommentRepository;
import com.bookreum.dev.domain.user.UserEntity;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentHeartService {

    private final CommentRepository commentRepository;
    private final CommentHeartRepository commentHeartRepository;

    @Transactional
    public void toggleHeart(Integer commentId, UserEntity user) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));

        commentHeartRepository.findByUserAndComment(user, comment)
                .ifPresentOrElse(
                        heart -> commentHeartRepository.delete(heart), // 공감 취소
                        () -> commentHeartRepository.save(CommentHeart.builder()
                                .user(user)
                                .comment(comment)
                                .build()) // 공감 등록
                );
    }

    public boolean hasHeart(Integer commentId, UserEntity user) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));

        return commentHeartRepository.findByUserAndComment(user, comment).isPresent();
    }

    public Long getHeartCount(Integer commentId) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));

        return commentHeartRepository.countByComment(comment);
    }
}
