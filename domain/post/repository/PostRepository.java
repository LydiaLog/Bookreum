package com.bookreum.domain.post.repository;

import com.bookreum.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllByOrderByCreatedAtDesc(); // 최신순

    List<Post> findAllByOrderByCreatedAtAsc(); // 오래된순

}
