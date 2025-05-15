package com.bookreum.dev.domain.post.service;

import com.bookreum.dev.domain.post.entity.PostEntity;
import com.bookreum.dev.domain.post.entity.PostHeart;
import com.bookreum.dev.domain.post.repository.PostHeartRepository;
import com.bookreum.dev.domain.post.repository.PostRepository;
import com.bookreum.dev.domain.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostHeartService {

    private final PostHeartRepository postHeartRepository;
    private final PostRepository postRepository;

    /**
     * ✅ 공감 토글 (추가 또는 취소)
     * @param postId 공감을 추가할 게시글 ID
     * @param user 공감을 추가하는 사용자
     * @return true -> 공감 추가됨, false -> 공감 취소됨
     */
    @Transactional
    public boolean toggleHeart(Integer postId, UserEntity user) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("📌 Post not found"));

        // ✅ 기존 공감 여부 확인
        PostHeart existingHeart = postHeartRepository.findByUserAndPost(user, post).orElse(null);

        if (existingHeart != null) {
            // ✅ 공감 취소
            postHeartRepository.delete(existingHeart);
            return false;
        }

        // ✅ 공감 추가
        PostHeart heart = PostHeart.builder()
                .user(user)
                .post(post)
                .build();
        postHeartRepository.save(heart);
        return true;
    }

    /**
     * ✅ 특정 게시글의 공감 수 조회
     * @param postId 공감 수를 조회할 게시글 ID
     * @return 공감 수 (long)
     */
 // ✅ 특정 게시글의 공감 수 조회
    public long countHearts(Integer postId) {
        return postHeartRepository.countByPostId(postId);
    }

}