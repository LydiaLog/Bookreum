package com.bookreum.dev.domain.club.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookreum.dev.domain.club.entity.ClubCommentEntity;
import com.bookreum.dev.domain.club.entity.CommentLikeEntity;
import com.bookreum.dev.domain.club.repository.CommentLikeRepository;
import com.bookreum.dev.domain.user.UserEntity;
import com.bookreum.dev.domain.user.UserService;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClubCommentLikeService {
    private final CommentLikeRepository likeRepo;
    private final ClubCommentService commentService;
    private final UserService userService;

    @Transactional
    public void like(Integer commentId, Integer userId) {
        ClubCommentEntity comment = commentService.getCommentEntity(commentId);
        UserEntity user = userService.getUserEntity(userId);
        if (likeRepo.existsByCommentAndUser(comment, user)) {
            throw new IllegalStateException("이미 좋아요를 눌렀습니다.");
        }
        likeRepo.save(CommentLikeEntity.builder()
            .comment(comment)
            .user(user)
            .build());
    }

    @Transactional
    public void unlike(Integer commentId, Integer userId) {
        ClubCommentEntity comment = commentService.getCommentEntity(commentId);
        UserEntity user = userService.getUserEntity(userId);
        likeRepo.deleteByCommentAndUser(comment, user);
    }

    @Transactional(readOnly=true)
    public long countLikes(Integer commentId) {
        ClubCommentEntity comment = commentService.getCommentEntity(commentId);
        return likeRepo.countByComment(comment);
    }

    @Transactional(readOnly=true)
    public boolean isLiked(Integer commentId, Integer userId) {
        ClubCommentEntity comment = commentService.getCommentEntity(commentId);
        UserEntity user = userService.getUserEntity(userId);
        return likeRepo.existsByCommentAndUser(comment, user);
    }
}

