package com.bookreum.dev.domain.club;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatMessageService messageService;
    private final SimpMessagingTemplate template;

    // 클라이언트가 "/app/chat.send/{clubId}" 로 보내면 이 메서드가 처리합니다.
    @MessageMapping("/chat.send/{clubId}")
    public void sendMessage(
        @DestinationVariable Long clubId,
        ChatMessageDTO dto
    ) {
        // 1) DB에 저장
        ChatMessageEntity saved = messageService.sendMessage(
            clubId,
            dto.getUserId(),
            dto.getContent()
        );

        // 2) "/topic/chat/{clubId}" 을 구독(subscribe)한 모든 클라이언트에게 브로드캐스트
        template.convertAndSend(
            "/topic/chat/" + clubId,
            ChatMessageDTO.fromEntity(saved)
        );  // <-- 여기서 ')' 로 닫아야 합니다!
    }
}
