package com.bookreum.dev.domain.club.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.bookreum.dev.domain.club.dto.ClubCommentDTO;
import com.bookreum.dev.domain.club.entity.ClubCommentEntity;
import com.bookreum.dev.domain.club.entity.ClubEntity;
import com.bookreum.dev.domain.club.entity.ClubStatus;
import com.bookreum.dev.domain.club.service.ClubApplicationService;
import com.bookreum.dev.domain.club.service.ClubCommentService;
import com.bookreum.dev.domain.club.service.ClubService;
import com.bookreum.dev.domain.user.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/clubs/{clubId}/comments")
@RequiredArgsConstructor
public class ClubCommentController {

    private final ClubCommentService commentService;
    private final ClubService clubService;
    private final UserService userService;
    private final ClubApplicationService applicationService;

    /**
     * 댓글 작성
     */
    @PostMapping
    public ResponseEntity<ClubCommentDTO> addComment(
        @PathVariable("clubId") Integer clubId,
        @RequestBody Map<String, String> payload
    ) {
        Integer userId = Integer.valueOf(payload.get("userId"));
        String content = payload.get("content");

        // 모임 참여자(호스트 포함) 및 마감 상태 체크
        authorizeUser(clubId, userId);

        ClubCommentEntity saved = commentService.addComment(clubId, userId, content);
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(ClubCommentDTO.fromEntity(saved));
    }

    /**
     * 댓글 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<ClubCommentDTO>> listComments(
    	@PathVariable("clubId") Integer clubId,
        @RequestParam("userId") Integer userId
    ) {
        authorizeUser(clubId, userId);

        List<ClubCommentDTO> dtos = commentService.listComments(clubId).stream()
            .map(ClubCommentDTO::fromEntity)
            .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    /**
     * 댓글 수정
     */
    @PutMapping("/{commentId}")
    public ResponseEntity<ClubCommentDTO> updateComment(
    	@PathVariable("clubId") Integer clubId,
    	@PathVariable("commentId") Integer commentId,
        @RequestBody Map<String, String> payload
    ) {
        Integer userId = Integer.valueOf(payload.get("userId"));
        String content = payload.get("content");

        ClubCommentEntity existing = commentService.getCommentEntity(commentId);
        if (!existing.getAuthor().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        ClubCommentEntity updated = commentService.updateComment(commentId, content);
        return ResponseEntity.ok(ClubCommentDTO.fromEntity(updated));
    }

    /**
     * 댓글 삭제
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
    	@PathVariable("clubId") Integer clubId,
    	@PathVariable("commentId") Integer commentId,
        @RequestParam("userId") Integer userId
    ) {
        ClubCommentEntity existing = commentService.getCommentEntity(commentId);

        boolean isAuthor = existing.getAuthor().getId().equals(userId);
        boolean isHost   = clubService.getClub(clubId).getUser().getId().equals(userId);

        if (!isAuthor && !isHost) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 모임 참여 권한(호스트 또는 신청자) 및 마감 상태 체크
     */
    private void authorizeUser(Integer clubId, Integer userId) {
        ClubEntity club = clubService.getClub(clubId);

        // 마감된 모임은 접근 금지
        if (club.getStatus() == ClubStatus.CLOSED) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "모집이 종료된 모임입니다.");
        }

        boolean isHost = club.getUser().getId().equals(userId);
        boolean isParticipant = applicationService.isApplied(
            club,
            userService.getUserEntity(userId)
        );

        if (!isHost && !isParticipant) {
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "해당 모임 참여자만 접근할 수 있습니다."
            );
        }
    }
}
