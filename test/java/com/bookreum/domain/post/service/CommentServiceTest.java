package com.bookreum.domain.post.service;

import com.bookreum.domain.book.entity.Book;
import com.bookreum.domain.post.dto.CommentDto;
import com.bookreum.domain.post.entity.Comment;
import com.bookreum.domain.post.entity.Post;
import com.bookreum.domain.post.repository.CommentRepository;
import com.bookreum.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CommentServiceTest {

    private CommentRepository commentRepository;
    private CommentService commentService;

    private User testUser;
    private Post testPost;

    @BeforeEach
    void setUp() {
        commentRepository = mock(CommentRepository.class);
        commentService = new CommentService(commentRepository);

        testUser = User.builder()
                .id(1L)
                .nickname("테스터")
                .build();

        testPost = Post.builder()
                .id(1L)
                .title("테스트 게시글")
                .content("내용")
                .user(testUser)
                .book(Book.builder().id(1L).title("테스트 책").build())
                .build();
    }

    @Test
    @DisplayName("댓글 생성 테스트")
    void createComment_shouldSaveCommentSuccessfully() {
        // given
        CommentDto.Request request = new CommentDto.Request();
        request.setContent("댓글 내용");
        request.setPostId(testPost.getId());
        request.setUserId(testUser.getId());

        when(commentRepository.save(any(Comment.class)))
                .thenAnswer(invocation -> {
                    Comment saved = invocation.getArgument(0);
                    saved = Comment.builder()
                            .id(1L)
                            .content(saved.getContent())
                            .user(saved.getUser())
                            .post(saved.getPost())
                            .createdAt(java.time.LocalDateTime.now())
                            .build();
                    return saved;
                });

        // when
        CommentDto.Response response = commentService.createComment(request, testUser, testPost);

        // then
        assertEquals("댓글 내용", response.getContent());
        assertEquals("테스터", response.getAuthorName());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    @DisplayName("댓글 삭제 테스트")
    void deleteComment_shouldCallRepositoryDeleteById() {
        commentService.deleteComment(1L);
        verify(commentRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("댓글 수정 테스트")
    void updateComment_shouldChangeContent() {
        // given
        Comment comment = Comment.builder()
                .id(1L)
                .content("이전 내용")
                .user(testUser)
                .post(testPost)
                .createdAt(java.time.LocalDateTime.now())
                .build();

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        // when
        commentService.updateComment(1L, "수정된 내용");

        // then
        assertEquals("수정된 내용", comment.getContent());
    }

    @Test
    @DisplayName("댓글 수정 실패 - 존재하지 않는 댓글")
    void updateComment_shouldThrowIfCommentNotFound() {
        when(commentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            commentService.updateComment(999L, "아무 내용");
        });
    }
}
