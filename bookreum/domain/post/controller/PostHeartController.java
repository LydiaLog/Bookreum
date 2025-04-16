package com.bookreum.domain.post.controller;

import com.bookreum.domain.post.service.PostHeartService;
import com.bookreum.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostHeartController {

    private final PostHeartService postHeartService;

    // toggle 방식 공감
    @PostMapping("/{id}/heart")
    public String toggleHeart(@PathVariable Long id) {
        // 임시 유저 (추후 로그인 연동)
        User user = User.builder().id(1L).nickname("테스터").build();

        boolean added = postHeartService.toggleHeart(id, user);
        return added ? "공감 추가됨" : "공감 취소됨";
    }

    // 공감 수 조회
    @GetMapping("/{id}/heart-count")
    public long countHearts(@PathVariable Long id) {
        return postHeartService.countHearts(id);
    }
}
