package com.bookreum.dev.domain.club;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.Value;
import lombok.RequiredArgsConstructor;

/**
 * 채팅 메시지 송신 및 조회 REST API
 */
@RestController
@RequestMapping("/api/clubs/{clubId}/chatroom/messages")
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    /**
     * 새로운 채팅 메시지를 전송합니다.
     * 
     * POST /api/clubs/{clubId}/chatroom/messages
     * {
     *   "userId": 123,
     *   "content": "안녕하세요!"
     * }
     */
    @PostMapping
    public ResponseEntity<ChatMessageEntity> sendMessage(
            @PathVariable Long clubId,
            @RequestBody SendMessageRequest req
    ) {
        ChatMessageEntity msg = chatMessageService.sendMessage(
            clubId,
            req.getUserId(),
            req.getContent()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(msg);
    }

    /**
     * 특정 채팅방의 모든 메시지를 조회합니다.
     * 
     * GET /api/clubs/{clubId}/chatroom/messages
     */
    @GetMapping
    public ResponseEntity<List<ChatMessageEntity>> getMessages(@PathVariable Long clubId) {
        List<ChatMessageEntity> list = chatMessageService.getMessages(clubId);
        return ResponseEntity.ok(list);
    }

    /**
     * 요청 바디를 위한 DTO
     */
    @Value
    public static class SendMessageRequest {
        Long userId;
        String content;
    }
}

