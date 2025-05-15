package com.bookreum.dev.domain.post.repository;

import com.bookreum.dev.domain.post.entity.PostEntity;
import com.bookreum.dev.domain.post.entity.PostHeart;
import com.bookreum.dev.domain.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

@Repository
public interface PostHeartRepository extends JpaRepository<PostHeart, Integer> {
	// ✅ 토글·단일 확인용
    Optional<PostHeart> findByUserAndPost(UserEntity user, PostEntity post);

    // ✅ 공감 수 세기 (ID 기준)
    long countByPostId(Integer postId);

    // ✅ 사용자별 좋아요 누른 내역 (페이징)
    Page<PostHeart> findByUser(UserEntity user, Pageable pageable);
}