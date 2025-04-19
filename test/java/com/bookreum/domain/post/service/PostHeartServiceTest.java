package com.bookreum.domain.post.service;

import com.bookreum.domain.book.entity.Book;
import com.bookreum.domain.post.entity.Post;
import com.bookreum.domain.post.entity.PostHeart;
import com.bookreum.domain.post.repository.PostHeartRepository;
import com.bookreum.domain.post.repository.PostRepository;
import com.bookreum.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PostHeartServiceTest {

    private PostRepository postRepository;
    private PostHeartRepository postHeartRepository;
    private PostHeartService postHeartService;

    private User testUser;
    private Post testPost;

    @BeforeEach
    void setUp() {
        postRepository = mock(PostRepository.class);
        postHeartRepository = mock(PostHeartRepository.class);
        postHeartService = new PostHeartService(postHeartRepository, postRepository);

        testUser = User.builder().id(1L).nickname("테스터").build();
        testPost = Post.builder()
                .id(1L)
                .title("테스트 게시글")
                .content("내용")
                .user(testUser)
                .book(Book.builder().id(1L).title("테스트 책").build())
                .build();
    }

    @Test
    @DisplayName("공감 추가 테스트 - 처음 누르는 경우")
    void toggleHeart_shouldAddHeartIfNotExists() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(postHeartRepository.findByUserAndPost(testUser, testPost)).thenReturn(Optional.empty());
        when(postHeartRepository.save(any(PostHeart.class))).thenAnswer(invocation -> {
            PostHeart heart = invocation.getArgument(0);
            return PostHeart.builder()
                    .id(1L)
                    .user(heart.getUser())
                    .post(heart.getPost())
                    .createdAt(java.time.LocalDateTime.now())
                    .build();
        });

        boolean result = postHeartService.toggleHeart(1L, testUser);

        assertTrue(result);
        verify(postHeartRepository, times(1)).save(any(PostHeart.class));
    }

    @Test
    @DisplayName("공감 취소 테스트 - 이미 누른 상태인 경우")
    void toggleHeart_shouldRemoveHeartIfExists() {
        PostHeart existingHeart = PostHeart.builder()
                .id(1L)
                .user(testUser)
                .post(testPost)
                .build();

        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(postHeartRepository.findByUserAndPost(testUser, testPost)).thenReturn(Optional.of(existingHeart));

        boolean result = postHeartService.toggleHeart(1L, testUser);

        assertFalse(result);
        verify(postHeartRepository, times(1)).delete(existingHeart);
    }

    @Test
    @DisplayName("공감 수 조회 테스트")
    void countHearts_shouldReturnCorrectCount() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(postHeartRepository.countByPost(testPost)).thenReturn(5L);

        long count = postHeartService.countHearts(1L);

        assertEquals(5L, count);
    }

    @Test
    @DisplayName("존재하지 않는 게시글 - toggleHeart 예외 발생")
    void toggleHeart_shouldThrowIfPostNotFound() {
        when(postRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            postHeartService.toggleHeart(999L, testUser);
        });
    }

    @Test
    @DisplayName("존재하지 않는 게시글 - countHearts 예외 발생")
    void countHearts_shouldThrowIfPostNotFound() {
        when(postRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            postHeartService.countHearts(999L);
        });
    }
}
