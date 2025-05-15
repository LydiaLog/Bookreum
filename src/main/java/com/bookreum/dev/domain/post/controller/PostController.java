package com.bookreum.dev.domain.post.controller;

import com.bookreum.dev.domain.post.dto.PostDto;
import com.bookreum.dev.domain.post.service.PostService;
import com.bookreum.dev.domain.book.AladinBookService;
import com.bookreum.dev.domain.book.BookEntity;
import com.bookreum.dev.domain.book.AladinItem;
import com.bookreum.dev.domain.book.BookRepository;
import com.bookreum.dev.domain.user.UserEntity;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final AladinBookService aladinBookService;
    private final BookRepository bookRepository;


    /**
     * 게시글 목록 (페이징 지원, 최신 순)
     */
    @GetMapping
    public ResponseEntity<Page<PostDto.Response>> getPosts(Pageable pageable) {
        Page<PostDto.Response> page = postService.getLatestPosts(pageable);
        return ResponseEntity.ok(page);
    }

    /**
     * 📌 키워드로 책 검색 (DB 저장 X, 검색 결과만 반환)
     */
    @GetMapping("/searchBooks")
    public ResponseEntity<List<AladinItem>> searchBooks(@RequestParam String keyword) {
        var result = aladinBookService.searchBooks(keyword);
        if (result.getTotalResults() == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No books found with the given keyword.");
        }
        return ResponseEntity.ok(result.getItem());
    }

    /**
     * 📌 사용자가 선택한 책 저장 (DB에 중복 저장 방지)
     */
    @PostMapping("/saveBook")
    @Transactional
    public ResponseEntity<BookEntity> saveSelectedBook(@RequestBody AladinItem selectedItem) {
        var existing = bookRepository.findByTitleAndAuthor(
            selectedItem.getTitle(), selectedItem.getAuthor()
        ).orElse(null);
        if (existing != null) {
            return ResponseEntity.ok(existing);
        }
        var newBook = BookEntity.builder()
                          .title(selectedItem.getTitle())
                          .author(selectedItem.getAuthor())
                          .coverImageUrl(selectedItem.getCover())
                          .build();
        return ResponseEntity.ok(bookRepository.save(newBook));
    }

    /**
     * 📌 게시글 생성 (사용자가 선택한 책 ID 사용)
     */
    @PostMapping
    @Transactional
    public ResponseEntity<PostDto.Response> createPost(
        @RequestParam("title") String title,
        @RequestParam("content") String content,
        @RequestParam("bookId") Integer bookId,
        @RequestParam(value = "coverUrl", required = false) String coverUrl,
        @RequestPart(value = "coverImage", required = false) MultipartFile coverImage
    ) {
        // 임시 유저 엔티티
        UserEntity user = UserEntity.builder()
            .id(1)
            .nickname("테스터")
            .build();

        var book = bookRepository.findById(bookId)
                      .orElseThrow(() -> new ResponseStatusException(
                          HttpStatus.NOT_FOUND,
                          "Book with ID " + bookId + " not found"
                      ));
        var finalUrl = postService.determineCoverImageUrl(coverImage, coverUrl, book);
        var response = postService.createPost(title, content, finalUrl, coverImage, user, book);
        return ResponseEntity.ok(response);
    }

    /**
     * 📌 게시글 수정
     */
    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> updatePost(
        @PathVariable Integer id,
        @RequestParam(required = false) String title,
        @RequestParam(required = false) String content,
        @RequestParam(required = false) Integer bookId,
        @RequestPart(value = "coverImage", required = false) MultipartFile image
    ) {
        postService.updatePost(id, title, content, bookId, image);
        return ResponseEntity.ok().build();
    }

    /**
     * 📌 게시글 상세 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<PostDto.DetailResponse> getPostById(@PathVariable Integer id) {
        return ResponseEntity.ok(postService.getPostDetail(id));
    }

    /**
     * 📌 게시글 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Integer id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}