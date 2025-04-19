
package com.bookreum.domain.post.service;

import com.bookreum.domain.book.entity.Book;
import com.bookreum.domain.post.dto.PostDto;
import com.bookreum.domain.post.entity.Post;
import com.bookreum.domain.post.repository.CommentRepository;
import com.bookreum.domain.post.repository.PostHeartRepository;
import com.bookreum.domain.post.repository.PostRepository;
import com.bookreum.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PostServiceTest {

    private PostRepository postRepository;
    private CommentRepository commentRepository;
    private PostHeartRepository postHeartRepository;
    private PostService postService;

    private User testUser;
    private Book testBook;
    private Post testPost;

    @BeforeEach
    void setUp() {
        postRepository = mock(PostRepository.class);
        commentRepository = mock(CommentRepository.class);
        postHeartRepository = mock(PostHeartRepository.class);
        postService = new PostService(postRepository, commentRepository, postHeartRepository);

        testUser = User.builder().id(1L).nickname("Tester").build();
        testBook = Book.builder().id(1L).title("Test Book").author("Author").build();
        testPost = Post.builder()
                .id(1L)
                .title("Sample Title")
                .content("Sample content")
                .imageUrl("sample.jpg")
                .user(testUser)
                .book(testBook)
                .build();
    }

    @Test
    @DisplayName("게시글 생성 테스트")
    void createPost_shouldCreateAndReturnPostDto() {
        PostDto.Request request = new PostDto.Request("Sample Title", "Sample content", "sample.jpg");

        when(postRepository.save(Mockito.any(Post.class))).thenReturn(testPost);

        PostDto.Response response = postService.createPost(request, testUser, testBook);

        assertEquals("Sample Title", response.getTitle());
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    @DisplayName("게시글 전체 최신순 조회")
    void getPostsSorted_latest() {
        when(postRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(testPost));
        when(postHeartRepository.countByPost(testPost)).thenReturn(3L);
        when(commentRepository.countByPost(testPost)).thenReturn(2L);

        List<PostDto.Response> result = postService.getPostsSorted("latest", testUser);

        assertEquals(1, result.size());
        assertEquals("Sample Title", result.get(0).getTitle());
    }

    @Test
    @DisplayName("게시글 상세 조회 - 존재하지 않으면 예외")
    void getPostDetail_notFound() {
        when(postRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            postService.getPostDetail(999L, testUser);
        });
    }

    @Test
    @DisplayName("게시글 수정 테스트")
    void updatePost_shouldUpdateValues() {
        PostDto.Request updateRequest = new PostDto.Request("Updated Title", "Updated content", "updated.jpg");
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));

        postService.updatePost(1L, updateRequest);

        assertEquals("Updated Title", testPost.getTitle());
    }

    @Test
    @DisplayName("게시글 삭제 테스트")
    void deletePost_shouldRemoveFromRepo() {
        postService.deletePost(1L);
        verify(postRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("게시글 검색 기능 테스트")
    void searchPosts_shouldReturnMatchingPosts() {
        when(postRepository.searchByKeywordOrderByLatest("sample")).thenReturn(List.of(testPost));
        when(postHeartRepository.countByPost(testPost)).thenReturn(1L);
        when(commentRepository.countByPost(testPost)).thenReturn(1L);

        List<PostDto.Response> results = postService.searchPosts("sample", "latest", testUser);

        assertFalse(results.isEmpty());
        assertEquals("Sample Title", results.get(0).getTitle());
    }
}
