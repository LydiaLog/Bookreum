package com.bookreum.domain.post.controller;

import com.bookreum.domain.post.dto.PostDto;
import com.bookreum.domain.post.service.PostService;
import com.bookreum.domain.book.entity.Book;
import com.bookreum.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    //게시글 작성
    @PostMapping
    public PostDto.Response createPost(@RequestBody PostDto.Request request) {
        // 임시 User, Book (추후 인증/조회 연동 예정)
        User user = User.builder().id(1L).nickname("테스터").build();
        Book book = Book.builder().id(1L).title("더미책").author("작가이름").build();

        return postService.createPost(request, user, book);
    }

    /**
     * 게시글 전체 조회 (정렬 포함)
     * sort = latest (최신순) | oldest (오래된순)
     */
    @GetMapping
    public List<PostDto.Response> getSortedPosts(@RequestParam(defaultValue = "latest") String sort) {
        User user = User.builder().id(1L).nickname("테스터").build(); // 임시 사용자
        return postService.getPostsSorted(sort, user);
    }

    //게시글 검색
    @GetMapping("/search")
    public List<PostDto.Response> searchPosts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "latest") String sort
    ) {
        User viewer = User.builder().id(1L).nickname("테스터").build(); // 임시 사용자
        return postService.searchPosts(keyword, sort, viewer);
    }

    //특정 게시글 상세 조회 (댓글 포함)
    @GetMapping("/{id}")
    public PostDto.DetailResponse getPostById(@PathVariable Long id) {
        User user = User.builder().id(1L).nickname("테스터").build(); // 임시 사용자
        return postService.getPostDetail(id, user);
    }

    //게시글 수정
    @PutMapping("/{id}")
    public void updatePost(@PathVariable Long id, @RequestBody PostDto.Request request) {
        postService.updatePost(id, request);
    }

    //게시글 삭제
    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable Long id) {
        postService.deletePost(id);
    }
}
