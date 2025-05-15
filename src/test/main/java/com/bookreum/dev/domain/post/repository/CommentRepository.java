package com.bookreum.dev.domain.post.repository;

import com.bookreum.dev.domain.post.entity.CommentEntity;
import com.bookreum.dev.domain.post.entity.PostEntity;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Integer> {
	 // ✅ User 정보를 함께 로드하도록 설정 (N+1 문제 예방)
    @EntityGraph(attributePaths = {"user"})
    List<CommentEntity> findByPostId(Integer postId);

    long countByPost(PostEntity post);
}