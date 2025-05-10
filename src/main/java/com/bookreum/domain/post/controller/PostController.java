package com.bookreum.domain.post.controller;

import com.bookreum.domain.post.dto.PostDto;
import com.bookreum.domain.post.service.PostService;
import com.bookreum.domain.book.entity.Book;
import com.bookreum.domain.user.entity.User;
import com.bookreum.domain.book.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final BookRepository bookRepository;

    @PostMapping
    public ResponseEntity<PostDto.Response> createPost(
            @RequestParam(value = "title") String title,
            @RequestParam(value = "content") String content,
            @RequestParam(value = "bookId") Long bookId,
            @RequestPart(value = "coverImage", required = false) MultipartFile image) {

        User user = User.builder().id(1L).nickname("테스터").build();
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

        return ResponseEntity.ok(postService.createPost(title, content, image, user, book));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updatePost(
            @PathVariable Long id,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) Long bookId,
            @RequestPart(value = "coverImage", required = false) MultipartFile image) {

        postService.updatePost(id, title, content, bookId, image);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<PostDto.Response>> getSortedPosts(@RequestParam(defaultValue = "latest") String sort) {
        return ResponseEntity.ok(sort.equals("latest") ? postService.getLatestPosts() : postService.getOldestPosts());
    }

    @GetMapping("/search")
    public ResponseEntity<List<PostDto.Response>> searchPosts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "latest") String sort) {
        return ResponseEntity.ok(postService.searchPosts(keyword, sort, null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDto.DetailResponse> getPostById(@PathVariable Long id) {
        User user = User.builder().id(1L).nickname("테스터").build();
        return ResponseEntity.ok(postService.getPostDetail(id, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}
