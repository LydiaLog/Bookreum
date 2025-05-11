package com.bookreum.domain.post.repository;

import com.bookreum.domain.post.entity.Comment;
import com.bookreum.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByPostId(Integer postId);
    long countByPost(Post post);
}
