package com.bookreum.dev.domain.club.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bookreum.dev.domain.club.entity.CommentLikeEntity;
import com.bookreum.dev.domain.club.entity.ClubCommentEntity;
import com.bookreum.dev.domain.user.UserEntity;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLikeEntity, Integer> {
    
	boolean existsByCommentAndUser(ClubCommentEntity comment, UserEntity user);
    Integer countByComment(ClubCommentEntity comment);
    Optional<CommentLikeEntity> findByCommentAndUser(ClubCommentEntity comment, UserEntity user);
    void deleteByCommentAndUser(ClubCommentEntity comment, UserEntity user);
}

