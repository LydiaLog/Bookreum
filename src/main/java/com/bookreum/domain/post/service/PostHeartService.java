package com.bookreum.domain.post.service;

import com.bookreum.domain.post.entity.Post;
import com.bookreum.domain.post.entity.PostHeart;
import com.bookreum.domain.post.repository.PostHeartRepository;
import com.bookreum.domain.post.repository.PostRepository;
import com.bookreum.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostHeartService {

    private final PostHeartRepository postHeartRepository;
    private final PostRepository postRepository;

    @Transactional
    public boolean toggleHeart(Long postId, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        return postHeartRepository.findByUserAndPost(user, post)
                .map(existing -> {
                    postHeartRepository.delete(existing);
                    return false; // 공감 취소됨
                })
                .orElseGet(() -> {
                    PostHeart heart = PostHeart.builder()
                            .user(user)
                            .post(post)
                            .build();
                    postHeartRepository.save(heart);
                    return true; // 공감 추가됨
                });
    }

    public long countHearts(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        return postHeartRepository.countByPost(post);
    }
}
