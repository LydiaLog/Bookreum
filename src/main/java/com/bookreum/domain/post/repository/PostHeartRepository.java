package com.bookreum.domain.post.repository;

import com.bookreum.domain.post.entity.Post;
import com.bookreum.domain.post.entity.PostHeart;
import com.bookreum.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostHeartRepository extends JpaRepository<PostHeart, Integer> {
    // ✅ 토글용 좋아요 여부 확인
    Optional<PostHeart> findByUserAndPost(User user, Post post);
    
    // ✅ 성능 개선된 공감 수 확인 (Post ID로 직접 조회)
    long countByPost_Id(Integer postId);
    
    // ✅ 좋아요 누른 글 조회용 (사용자별)
    List<PostHeart> findByUser(User user);
}
