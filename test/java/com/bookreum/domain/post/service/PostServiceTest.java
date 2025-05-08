package com.bookreum.domain.post.service;

import com.bookreum.domain.book.entity.Book;
import com.bookreum.domain.post.dto.PostDto;
import com.bookreum.domain.post.entity.Post;
import com.bookreum.domain.post.repository.PostRepository;
import com.bookreum.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityManager;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional  // 클래스 전체에 트랜잭션 적용 ✅
class PostServiceTest {

    @Autowired 
    private PostService postService;
    
    @Autowired 
    private PostRepository postRepository;

    @Autowired 
    private EntityManager em;

    private User author;
    private Book book;

    @BeforeEach
    void setUp() {
        // 테스트마다 fresh한 사용자/도서 엔티티 저장
        author = User.builder().nickname("통합테스터").profileImage("https://example.com/profile.jpg").build();
        book   = Book.builder().title("테스트 북").author("저자").coverImageUrl("https://example.com/book.jpg").build();

        em.persist(author);
        em.persist(book);
        em.flush();
    }

    @Test
    void createPost_persistsToMySQL() {
        // given
        String title = "첫 번째 글";
        String content = "본문입니다.";
        MultipartFile image = null; // 이미지 없이 테스트

        // when
        PostDto.Response response = postService.createPost(title, content, image, author, book);

        // then : 트랜잭션이 commit 된 뒤 실제 MySQL에 row 가 있는지 확인
        em.flush();
        em.clear(); // 영속성 컨텍스트 초기화

        Post savedPost = postRepository.findById(response.getId()).orElseThrow();

        assertThat(savedPost).isNotNull();
        assertThat(savedPost.getTitle()).isEqualTo(title);
        assertThat(savedPost.getContent()).isEqualTo(content);
        assertThat(savedPost.getUser().getId()).isEqualTo(author.getId());
        assertThat(savedPost.getBook().getId()).isEqualTo(book.getId());
    }
}
