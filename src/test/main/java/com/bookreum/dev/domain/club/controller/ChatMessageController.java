package com.bookreum.dev.domain.club.controller;

import com.bookreum.dev.domain.club.dto.ChatMessageDTO;
import com.bookreum.dev.domain.club.entity.ChatMessageEntity;
import com.bookreum.dev.domain.club.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatMessageController {
    private final ChatMessageService messageService;

    @GetMapping("/{clubId}/messages")
    public ResponseEntity<List<ChatMessageDTO>> getMessages(
        @PathVariable Long clubId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        List<ChatMessageDTO> messages = messageService.getMessages(clubId, page, size)
                .stream()
                .map(ChatMessageDTO::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(messages);
    }

    @PostMapping("/{clubId}/messages")
    public ResponseEntity<ChatMessageDTO> sendMessage(
        @PathVariable Long clubId,
        @RequestBody ChatMessageDTO dto
    ) {
        ChatMessageEntity saved = messageService.sendMessage(clubId, dto.getUserId(), dto.getContent());
        return ResponseEntity.ok(ChatMessageDTO.fromEntity(saved));
    }

    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long messageId) {
        messageService.deleteMessage(messageId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{clubId}/messages")
    public ResponseEntity<Void> deleteAllMessages(@PathVariable Long clubId) {
        messageService.deleteAllMessagesInRoom(clubId);
        return ResponseEntity.noContent().build();
    }
}
