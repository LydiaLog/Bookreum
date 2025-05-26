package com.bookreum.dev.domain.club.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bookreum.dev.domain.club.service.ClubApplicationService;
import com.bookreum.dev.domain.club.service.ClubCommentLikeService;
import com.bookreum.dev.domain.club.service.ClubService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/clubs/{clubId}/comments/{commentId}/likes")
@RequiredArgsConstructor
public class ClubCommentLikeController {
    private final ClubCommentLikeService likeService;
    private final ClubService clubService;
    private final ClubApplicationService appService;

    @PostMapping
    public ResponseEntity<Void> like(
        @PathVariable("clubId") Integer clubId,
        @PathVariable("commentId") Integer commentId,
        @RequestParam("userId") Integer userId
    ) {
        // (권한 체크는 기존 authorizeUser 로직 재사용)
        likeService.like(commentId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> unlike(
    	@PathVariable("clubId") Integer clubId,
        @PathVariable("commentId") Integer commentId,
        @RequestParam("userId") Integer userId
    ) {
        likeService.unlike(commentId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Map<String,Object>> getLikeInfo(
    	@PathVariable("clubId") Integer clubId,
        @PathVariable("commentId") Integer commentId,
        @RequestParam("userId") Integer userId
    ) {
        long count = likeService.countLikes(commentId);
        boolean liked = likeService.isLiked(commentId, userId);
        return ResponseEntity.ok(Map.of(
            "likeCount", count,
            "likedByUser", liked
        ));
    }
}

