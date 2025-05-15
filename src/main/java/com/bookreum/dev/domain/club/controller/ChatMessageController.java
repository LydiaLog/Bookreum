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
    public ResponseEntity<List<ChatMessageDTO>> getMessages(
            @PathVariable Integer clubId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<ChatMessageDTO> dtos = messageService.getMessages(clubId, page, size).stream()
                .map(ChatMessageDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * 새로운 채팅 메시지 전송
     * @param clubId 모임 ID
     * @param dto 메시지 요청 DTO
     * @return 저장된 메시지 DTO와 HTTP 201
     */
    @PostMapping
    public ResponseEntity<ChatMessageDTO> sendMessage(
            @PathVariable Integer clubId,
            @RequestBody @Valid ChatMessageDTO dto
    ) {
        ChatMessageEntity saved = messageService.sendMessage(clubId, dto.getUserId(), dto.getContent());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ChatMessageDTO.fromEntity(saved));
    }

    /**
     * 특정 메시지 삭제
     * @param clubId 모임 ID (unused)
     * @param messageId 메시지 ID
     * @return HTTP 204
     */
    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(
            @PathVariable Integer clubId,
            @PathVariable Integer messageId
    ) {
        messageService.deleteMessage(messageId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 채팅방 내 모든 메시지 삭제
     * @param clubId 모임 ID
     * @return HTTP 204
     */
    @DeleteMapping
    public ResponseEntity<Void> deleteAllMessages(@PathVariable Integer clubId) {
        messageService.deleteAllMessagesInRoom(clubId);
        return ResponseEntity.noContent().build();
    }
}
