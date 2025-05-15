package com.bookreum.dev.domain.post.repository;

import com.bookreum.dev.domain.post.entity.PostEntity;
import com.bookreum.dev.domain.post.entity.PostHeart;
import com.bookreum.dev.domain.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostHeartRepository extends JpaRepository<PostHeart, Integer> {
	 // ✅ 토글용 좋아요 여부 확인
    Optional<PostHeart> findByUserAndPost(UserEntity user, PostEntity post);
    
 // ✅ 공감 수 확인 (Post ID로 직접 조회)
    @Query("SELECT COUNT(ph) FROM PostHeart ph WHERE ph.post.id = :postId")
    Integer countByPostId(@Param("postId") Integer postId);
    
    // ✅ 좋아요 누른 글 조회용 (사용자별)
    List<PostHeart> findByUser(UserEntity user);
}