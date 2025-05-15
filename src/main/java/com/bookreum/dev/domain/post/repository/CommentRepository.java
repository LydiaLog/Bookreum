package com.bookreum.dev.domain.post.repository;

import com.bookreum.dev.domain.post.entity.CommentEntity;
import com.bookreum.dev.domain.post.entity.PostEntity;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Integer> {
	   // ✅ 특정 포스트의 댓글을 생성일 내림차순으로 조회
	List<CommentEntity> findByPostIdOrderByCreatedAtDesc(Integer postId);

    // ✅ 댓글 개수를 postId로 바로 세기 (서비스 호출 편의)
    long countByPostId(Integer postId);
    
 // 추가: 엔티티 기반 조회 메서드
    List<CommentEntity> findByPostOrderByCreatedAtDesc(PostEntity post);
    
    
    
}