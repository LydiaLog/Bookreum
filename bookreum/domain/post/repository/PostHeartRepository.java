package com.bookreum.domain.post.repository;

import com.bookreum.domain.post.entity.PostHeart;
import com.bookreum.domain.post.entity.Post;
import com.bookreum.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostHeartRepository extends JpaRepository<PostHeart, Long> {
    Optional<PostHeart> findByUserAndPost(User user, Post post);
    long countByPost(Post post);
}
