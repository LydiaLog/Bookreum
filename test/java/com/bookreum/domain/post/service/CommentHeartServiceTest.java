package com.bookreum.domain.post.service;

import com.bookreum.domain.book.entity.Book;
import com.bookreum.domain.post.entity.Comment;
import com.bookreum.domain.post.entity.CommentHeart;
import com.bookreum.domain.post.entity.Post;
import com.bookreum.domain.post.repository.CommentHeartRepository;
import com.bookreum.domain.post.repository.CommentRepository;
import com.bookreum.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CommentHeartServiceTest {

    private CommentRepository commentRepository;
    private CommentHeartRepository commentHeartRepository;
    private CommentHeartService commentHeartService;

    private User testUser;
    private Comment testComment;

    @BeforeEach
    void setUp() {
        commentRepository = mock(CommentRepository.class);
        commentHeartRepository = mock(CommentHeartRepository.class);
        commentHeartService = new CommentHeartService(commentRepository, commentHeartRepository);

        testUser = User.builder()
                .id(1L)
                .nickname("테스터")
                .build();

        testComment = Comment.builder()
                .id(1L)
                .content("댓글 내용")
                .user(testUser)
                .post(Post.builder()
                        .id(1L)
                        .title("테스트 게시글")
                        .book(Book.builder().id(1L).title("테스트 책").build())
                        .user(testUser)
                        .build())
                .build();
    }

    @Test
    @DisplayName("공감 추가 테스트 - 처음 누른 경우")
    void toggleHeart_shouldAddHeartIfNotExists() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));
        when(commentHeartRepository.findByUserAndComment(testUser, testComment)).thenReturn(Optional.empty());

        commentHeartService.toggleHeart(1L, testUser);

        verify(commentHeartRepository, times(1)).save(any(CommentHeart.class));
        verify(commentHeartRepository, never()).delete(any(CommentHeart.class));
    }

    @Test
    @DisplayName("공감 취소 테스트 - 이미 누른 경우")
    void toggleHeart_shouldRemoveHeartIfExists() {
        CommentHeart existingHeart = CommentHeart.builder()
                .id(1L)
                .user(testUser)
                .comment(testComment)
                .build();

        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));
        when(commentHeartRepository.findByUserAndComment(testUser, testComment)).thenReturn(Optional.of(existingHeart));

        commentHeartService.toggleHeart(1L, testUser);

        verify(commentHeartRepository, times(1)).delete(existingHeart);
        verify(commentHeartRepository, never()).save(any(CommentHeart.class));
    }

    @Test
    @DisplayName("공감 여부 확인 테스트")
    void hasHeart_shouldReturnTrueIfExists() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));
        when(commentHeartRepository.findByUserAndComment(testUser, testComment)).thenReturn(Optional.of(mock(CommentHeart.class)));

        boolean result = commentHeartService.hasHeart(1L, testUser);

        assertTrue(result);
    }

    @Test
    @DisplayName("공감 여부 확인 테스트 - 존재하지 않으면 false")
    void hasHeart_shouldReturnFalseIfNotExists() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));
        when(commentHeartRepository.findByUserAndComment(testUser, testComment)).thenReturn(Optional.empty());

        boolean result = commentHeartService.hasHeart(1L, testUser);

        assertFalse(result);
    }

    @Test
    @DisplayName("공감 수 조회 테스트")
    void getHeartCount_shouldReturnCorrectCount() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));
        when(commentHeartRepository.countByComment(testComment)).thenReturn(7L);

        Long result = commentHeartService.getHeartCount(1L);

        assertEquals(7L, result);
    }

    @Test
    @DisplayName("존재하지 않는 댓글일 경우 예외 발생")
    void toggleHeart_shouldThrowIfCommentNotFound() {
        when(commentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            commentHeartService.toggleHeart(999L, testUser);
        });
    }
}
