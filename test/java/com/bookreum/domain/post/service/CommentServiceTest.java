package com.bookreum.domain.post.service;

import com.bookreum.domain.post.dto.CommentDto;
import com.bookreum.domain.post.entity.Comment;
import com.bookreum.domain.post.entity.Post;
import com.bookreum.domain.post.repository.CommentRepository;
import com.bookreum.domain.post.repository.PostRepository;
import com.bookreum.domain.user.entity.User;
import com.bookreum.domain.user.repository.UserRepository;
import com.bookreum.domain.book.entity.Book;
import com.bookreum.domain.book.repository.BookRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BookRepository bookRepository;

    @PersistenceContext
    private EntityManager em;

    private User user;
    private Post post;
    private Book book;

    @BeforeEach
    void setUp() {
        // 사용자 생성
        user = userRepository.save(User.builder().nickname("테스트 사용자").build());

        // 책 생성
        book = bookRepository.save(Book.builder().title("테스트 책").author("테스트 저자").build());

        // 게시글 생성
        post = postRepository.save(Post.builder()
                .title("테스트 글")
                .content("테스트 내용")
                .user(user)
                .book(book)
                .build());

        em.flush();
        em.clear();
    }

    @Test
    void createComment_persistsToDatabase() {
        // given
        CommentDto.Request request = new CommentDto.Request();
        request.setContent("댓글 내용");

        // when
        CommentDto.Response response = commentService.createComment(request, user, post);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).isEqualTo("댓글 내용");
        assertThat(response.getAuthorName()).isEqualTo(user.getNickname());

        // DB에서 댓글 확인
        List<Comment> comments = commentRepository.findAll();
        assertThat(comments).hasSize(1);
        assertThat(comments.get(0).getContent()).isEqualTo("댓글 내용");
        assertThat(comments.get(0).getUser().getNickname()).isEqualTo(user.getNickname());
    }

    @Test
    void getAllCommentsForPost() {
        // given
        CommentDto.Request request1 = new CommentDto.Request();
        request1.setContent("첫 번째 댓글");
        commentService.createComment(request1, user, post);

        CommentDto.Request request2 = new CommentDto.Request();
        request2.setContent("두 번째 댓글");
        commentService.createComment(request2, user, post);

        // when
        List<CommentDto.Response> comments = commentService.getCommentsByPost(post.getId());

        // then
        assertThat(comments).hasSize(2);
        assertThat(comments.get(0).getContent()).isEqualTo("첫 번째 댓글");
        assertThat(comments.get(1).getContent()).isEqualTo("두 번째 댓글");
    }

    @Test
    void deleteComment_removesFromDatabase() {
        // given
        CommentDto.Request request = new CommentDto.Request();
        request.setContent("댓글 내용");
        CommentDto.Response response = commentService.createComment(request, user, post);
        Long commentId = response.getId();

        // when
        commentService.deleteComment(commentId);

        // then
        List<Comment> comments = commentRepository.findAll();
        assertThat(comments).isEmpty();
    }

    @Test
    void updateComment_changesContent() {
        // given
        CommentDto.Request request = new CommentDto.Request();
        request.setContent("원래 댓글 내용");
        CommentDto.Response response = commentService.createComment(request, user, post);
        Long commentId = response.getId();

        // when
        commentService.updateComment(commentId, "수정된 댓글 내용");

        // then
        Comment updatedComment = commentRepository.findById(commentId).orElseThrow();
        assertThat(updatedComment.getContent()).isEqualTo("수정된 댓글 내용");
    }
}
