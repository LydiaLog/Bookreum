// UserController.java
package com.bookreum.dev.domain.user;

import com.bookreum.dev.domain.post.dto.PostDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 내가 작성한 게시글 목록 조회
     * GET /api/users/{userId}/posts
     */
    @GetMapping("/{userId}/posts")
    public ResponseEntity<Page<PostDto.Response>> getMyPosts(
        @PathVariable Integer userId,
        Pageable pageable
    ) {
        Page<PostDto.Response> page = userService.getPostsByUser(userId, pageable);
        return ResponseEntity.ok(page);
    }

    /**
     * 내가 좋아요 누른 게시글 목록 조회
     * GET /api/users/{userId}/liked-posts
     */
    @GetMapping("/{userId}/liked-posts")
    public ResponseEntity<Page<PostDto.Response>> getLikedPosts(
        @PathVariable Integer userId,
        Pageable pageable
    ) {
        Page<PostDto.Response> page = userService.getLikedPosts(userId, pageable);
        return ResponseEntity.ok(page);
    }
}
