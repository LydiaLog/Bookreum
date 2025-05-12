package com.bookreum.dev.domain.post.controller;

import com.bookreum.dev.domain.post.dto.PostDto;
import com.bookreum.dev.domain.post.service.PostService;
import com.bookreum.dev.domain.book.BookEntity;
import com.bookreum.dev.domain.user.UserEntity;
import com.bookreum.dev.domain.book.BookRepository;
import com.bookreum.dev.domain.book.AladinBookService;
import com.bookreum.dev.domain.book.AladinSearchResult;
import com.bookreum.dev.domain.book.AladinItem;
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
    private final AladinBookService aladinBookService;
    private final BookRepository bookRepository;

    @PersistenceContext
    private EntityManager em;

    /**
     * 📌 키워드로 책 검색 (DB 저장 X, 검색 결과만 반환)
     * @param keyword 검색할 키워드 (책 제목 또는 저자)
     * @return 검색된 책 목록 (List<AladinItem>)
     */
    @GetMapping("/searchBooks")
    public ResponseEntity<List<AladinItem>> searchBooks(@RequestParam String keyword) {
        AladinSearchResult result = aladinBookService.searchBooks(keyword);

        // 검색 결과가 없으면 404 에러 반환
        if (result.getTotalResults() == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No books found with the given keyword.");
        }
        return ResponseEntity.ok(result.getItem());
    }

    /**
     * 📌 사용자가 선택한 책 저장 (DB에 중복 저장 방지)
     * @param selectedItem 사용자가 선택한 책 정보 (AladinItem)
     * @return 저장된 책 정보 (Book)
     */
    @PostMapping("/saveBook")
    @Transactional
    public ResponseEntity<BookEntity> saveSelectedBook(@RequestBody AladinItem selectedItem) {
        // 이미 저장된 책이 있는지 확인
        BookEntity existingBook = bookRepository.findByTitleAndAuthor(
                selectedItem.getTitle(), selectedItem.getAuthor()
        ).orElse(null);

        // 기존 책이 있다면 그대로 반환
        if (existingBook != null) {
            return ResponseEntity.ok(existingBook);
        }

        // 새로운 책 저장
        BookEntity newBook = BookEntity.builder()
                .title(selectedItem.getTitle())
                .author(selectedItem.getAuthor())
                .coverImageUrl(selectedItem.getCover())
                .build();

        BookEntity savedBook = bookRepository.save(newBook);
        return ResponseEntity.ok(savedBook);
    }

    /**
     * 📌 게시글 생성 (사용자가 선택한 책 ID 사용)
     * @param title 게시글 제목
     * @param content 게시글 내용
     * @param bookId 선택한 책 ID
     * @param coverUrl 기본 커버 이미지 URL (선택)
     * @param coverImage 사용자 업로드 이미지 (선택)
     * @return 생성된 게시글 응답 (PostDto.Response)
     */
    @PostMapping
    @Transactional
    public ResponseEntity<PostDto.Response> createPost(
            @RequestParam(value = "title") String title,
            @RequestParam(value = "content") String content,
            @RequestParam(value = "bookId") Integer bookId,
            @RequestParam(value = "coverUrl", required = false) String coverUrl,
            @RequestPart(value = "coverImage", required = false) MultipartFile coverImage) {
        System.out.println("📌 createPost 호출됨");
        System.out.println("📌 title: " + title);
        System.out.println("📌 content: " + content);
        System.out.println("📌 bookId: " + bookId);

        // 임시 사용자 (테스트용)
        UserEntity user = UserEntity.builder().id(1).nickname("테스터").build();
        
        // 사용자가 선택한 책 ID로 책 조회
        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book with ID " + bookId + " not found"));

        // 최종 커버 이미지 URL 결정 (사용자 이미지 > 기본 URL > 책 이미지)
        String finalCoverImageUrl = postService.determineCoverImageUrl(coverImage, coverUrl, book);
        
        // 게시글 생성 및 저장
        return ResponseEntity.ok(postService.createPost(title, content, finalCoverImageUrl, coverImage, user, book));
    }

    /**
     * 📌 게시글 수정
     * @param id 수정할 게시글 ID
     * @param title 수정할 제목 (선택)
     * @param content 수정할 내용 (선택)
     * @param bookId 수정할 책 ID (선택)
     * @param image 수정할 이미지 (선택)
     * @return 수정된 게시글 응답 (200 OK)
     */
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


    /**
     * 📌 게시글 상세 조회
     * @param id 게시글 ID
     * @return 상세 게시글 응답
     */
    @GetMapping("/{id}")
    public ResponseEntity<PostDto.DetailResponse> getPostById(@PathVariable("id") Integer id) {
        System.out.println("📌 Requested Post ID: " + id);
        UserEntity user = UserEntity.builder().id(1).nickname("테스터").build();
        return ResponseEntity.ok(postService.getPostDetail(id, user));
    }


    /**
     * 📌 게시글 삭제
     * @param id 삭제할 게시글 ID
     * @return 상태 코드 204 (No Content)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Integer id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}
