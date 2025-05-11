package com.bookreum.domain.post.controller;

import com.bookreum.domain.post.dto.PostDto;
import com.bookreum.domain.post.service.PostService;
import com.bookreum.domain.book.entity.Book;
import com.bookreum.domain.user.entity.User;
import com.bookreum.domain.book.repository.BookRepository;
import com.bookreum.external.aladin.AladinBookService;
import com.bookreum.external.aladin.AladinSearchResult;
import com.bookreum.external.aladin.AladinItem;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final BookRepository bookRepository;
    private final AladinBookService aladinBookService;

    @PersistenceContext
    private EntityManager em;

    @PostMapping
    @Transactional
    public ResponseEntity<PostDto.Response> createPost(
            @RequestParam(value = "title") String title,
            @RequestParam(value = "content") String content,
            @RequestParam(value = "bookId", required = false) Integer bookId,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "coverUrl", required = false) String coverUrl,
            @RequestPart(value = "coverImage", required = false) MultipartFile coverImage) {
        System.out.println("📌 createPost 호출됨");
        System.out.println("📌 title: " + title);
        System.out.println("📌 content: " + content);
        System.out.println("📌 bookId: " + bookId);
        System.out.println("📌 keyword: " + keyword);

        User user = User.builder().id(1).nickname("테스터").build();
        Book book;

        try {
            book = postService.determineBook(bookId, keyword);
        } catch (ResponseStatusException ex) {
            throw ex;
        }

        String finalCoverImageUrl = postService.determineCoverImageUrl(coverImage, coverUrl, book);
        return ResponseEntity.ok(postService.createPost(title, content, finalCoverImageUrl, coverImage, user, book));
    }

    // 🔍 키워드로 알라딘 API에서 책 검색 및 저장
    @Transactional
    private Book searchAndSaveBookByKeyword(String keyword) {
        AladinSearchResult result = aladinBookService.searchBooks(keyword);

        if (result.getTotalResults() > 0) {
            AladinItem aladinItem = result.getItem().get(0);

            Book existingBook = bookRepository.findByTitleAndAuthor(aladinItem.getTitle(), aladinItem.getAuthor()).orElse(null);
            if (existingBook != null) {
                System.out.println("📌 기존 책 발견: " + existingBook.getTitle());
                return existingBook;
            }

            Book newBook = Book.builder()
                    .title(aladinItem.getTitle())
                    .author(aladinItem.getAuthor())
                    .coverImageUrl(aladinItem.getCover())
                    .build();
            return bookRepository.save(newBook);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No books found with the given keyword.");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updatePost(
            @PathVariable Integer id,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) Integer bookId,
            @RequestPart(value = "coverImage", required = false) MultipartFile image) {

        postService.updatePost(id, title, content, bookId, image);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<PostDto.Response>> getSortedPosts(
            @RequestParam(defaultValue = "latest") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        List<PostDto.Response> posts = sort.equals("latest") 
            ? postService.getLatestPosts(pageable)
            : postService.getOldestPosts(pageable);

        return ResponseEntity.ok(posts);
    }

    @GetMapping("/search")
    public ResponseEntity<List<PostDto.Response>> searchPosts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "latest") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        List<PostDto.Response> posts = postService.searchPosts(keyword, sort, pageable);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDto.DetailResponse> getPostById(@PathVariable Integer id) {
        User user = User.builder().id(1).nickname("테스터").build();
        return ResponseEntity.ok(postService.getPostDetail(id, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Integer id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}
