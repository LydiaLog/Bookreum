package com.bookreum.domain.post.repository;

import com.bookreum.domain.post.entity.Post;
import com.bookreum.domain.post.entity.PostHeart;
import com.bookreum.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostHeartRepository extends JpaRepository<PostHeart, Integer> {
	//토글용 좋아요 여부 확인
    Optional<PostHeart> findByUserAndPost(User user, Post post);
    //글별 공감 수 확인
    long countByPost(Post post);
 // 좋아요 누른 글 조회용
    List<PostHeart> findByUser(User user); 
}
