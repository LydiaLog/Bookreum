package com.bookreum.dev.domain.club.controller;

import com.bookreum.dev.domain.club.dto.ChatMessageDTO;
import com.bookreum.dev.domain.club.entity.ChatMessageEntity;
import com.bookreum.dev.domain.club.service.ChatMessageService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 채팅 메시지 발송 및 조회 REST API
 */
@RestController
@RequestMapping("/api/clubs/{clubId}/messages")
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService messageService;

    /**
     * 특정 모임의 채팅 메시지 목록 조회 (페이징)
     * @param clubId 모임 ID
     * @param page 페이지 번호 (0-based)
     * @param size 페이지 크기
     * @return 메시지 DTO 리스트
     */
    @GetMapping
    public ResponseEntity<?> getMessages(
            @PathVariable(name = "clubId") Integer clubId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        try {
            List<ChatMessageDTO> dtos = messageService.getMessages(clubId, page, size).stream()
                    .map(ChatMessageDTO::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("존재하지 않는 모임입니다.");
        }
    }

    /**
     * 새로운 채팅 메시지 전송
     * @param clubId 모임 ID
     * @param dto 메시지 요청 DTO
     * @return 저장된 메시지 DTO와 HTTP 201
     */
    @PostMapping
    public ResponseEntity<?> sendMessage(
            @PathVariable(name = "clubId") Integer clubId,
            @RequestBody @Valid ChatMessageDTO dto
    ) {
        try {
            ChatMessageEntity saved = messageService.sendMessage(clubId, dto.getUserId(), dto.getContent());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ChatMessageDTO.fromEntity(saved));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("존재하지 않는 모임입니다.");
        }
    }

    /**
     * 특정 메시지 삭제
     * @param clubId 모임 ID (unused)
     * @param messageId 메시지 ID
     * @return HTTP 204
     */
    @DeleteMapping("/{messageId}")
    public ResponseEntity<?> deleteMessage(
            @PathVariable(name = "clubId") Integer clubId,
            @PathVariable(name = "messageId") Integer messageId
    ) {
        try {
            messageService.deleteMessage(messageId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("존재하지 않는 메시지입니다.");
        }
    }

    /**
     * 채팅방 내 모든 메시지 삭제
     * @param clubId 모임 ID
     * @return HTTP 204
     */
    @DeleteMapping
    public ResponseEntity<?> deleteAllMessages(@PathVariable(name = "clubId") Integer clubId) {
        try {
            messageService.deleteAllMessagesInRoom(clubId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("존재하지 않는 모임입니다.");
        }
    }
}