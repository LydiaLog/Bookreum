package com.bookreum.dev.domain.club.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import com.bookreum.dev.domain.club.dto.ChatMessageDTO;
import com.bookreum.dev.domain.club.entity.ChatMessageEntity;
import com.bookreum.dev.domain.club.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {
    private static final Logger logger = LoggerFactory.getLogger(ChatWebSocketController.class);
    private final ChatMessageService messageService;
    private final SimpMessagingTemplate template;

    @MessageMapping("/chat.send/{clubId}")
    public void sendMessage(
        @DestinationVariable Long clubId,
        ChatMessageDTO dto
    ) {
        logger.info("Received WebSocket message for club {}: {}", clubId, dto);

        // 메시지 저장
        ChatMessageEntity saved = messageService.sendMessage(clubId, dto.getUserId(), dto.getContent());

        // 클라이언트에 메시지 브로드캐스트
        template.convertAndSend("/topic/chat/" + clubId, ChatMessageDTO.fromEntity(saved));
    }
}
