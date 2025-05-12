package com.bookreum.dev.domain.post.repository;

import com.bookreum.dev.domain.post.entity.CommentEntity;
import com.bookreum.dev.domain.post.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Integer> {
    List<CommentEntity> findByPostId(Integer postId);
    long countByPost(PostEntity post);
}