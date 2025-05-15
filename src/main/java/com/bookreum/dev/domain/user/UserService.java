package com.bookreum.dev.domain.user;

import com.bookreum.dev.domain.post.dto.PostDto;
import com.bookreum.dev.domain.post.entity.PostHeart;
import com.bookreum.dev.domain.post.repository.PostHeartRepository;
import com.bookreum.dev.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostHeartRepository postHeartRepository;

    /**
     * 특정 사용자가 작성한 게시글 목록 조회 (최신 순)
     * @param userId 사용자 PK
     * @param pageable 페이지 정보
     * @return Page<PostDto.Response>
     */
    public Page<PostDto.Response> getPostsByUser(Integer userId, Pageable pageable) {
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found, id: " + userId));

        return postRepository.findByUserOrderByCreatedAtDesc(user, pageable)
            .map(post -> {
                long heartCount = postHeartRepository.countByPostId(post.getId());
                // 댓글 수는 아직 계산하지 않음 (0L 사용)
                return PostDto.Response.fromEntity(post, post.getUser(), heartCount, 0L);
            });
    }

    /**
     * 특정 사용자가 좋아요 누른 게시글 목록 조회
     * @param userId 사용자 PK
     * @param pageable 페이지 정보
     * @return Page<PostDto.Response>
     */
    public Page<PostDto.Response> getLikedPosts(Integer userId, Pageable pageable) {
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found, id: " + userId));

        return postHeartRepository.findByUser(user, pageable)
            .map(PostHeart::getPost)
            .map(post -> {
                long heartCount = postHeartRepository.countByPostId(post.getId());
                return PostDto.Response.fromEntity(post, post.getUser(), heartCount, 0L);
            });
    }
}