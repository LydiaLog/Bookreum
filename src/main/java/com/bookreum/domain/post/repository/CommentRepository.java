package com.bookreum.domain.post.repository;

import com.bookreum.domain.post.entity.Comment;
import com.bookreum.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostId(Long postId);
    long countByPost(Post post); 
}
