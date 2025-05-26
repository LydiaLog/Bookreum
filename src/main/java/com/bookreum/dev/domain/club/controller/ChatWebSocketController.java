//package com.bookreum.dev.domain.club.controller;
//
//import org.springframework.messaging.handler.annotation.DestinationVariable;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.handler.annotation.Payload;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Controller;
//
//import com.bookreum.dev.domain.club.dto.ChatMessageDTO;
//import com.bookreum.dev.domain.club.entity.ChatMessageEntity;
//import com.bookreum.dev.domain.club.service.ChatMessageService;
//
//import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
///**
// * WebSocket을 통한 채팅 메시지 송수신 컨트롤러
// * <p>
// * 클라이언트가 '/app/chat.send/{clubId}'로 메시지를 전송하면,
// * 해당 clubId의 채팅방에 메시지를 저장하고 브로드캐스트합니다.
// */
//@Controller
//@RequiredArgsConstructor
//public class ChatWebSocketController {
//    private static final Logger logger = LoggerFactory.getLogger(ChatWebSocketController.class);
//
//    private final ChatMessageService messageService;
//    private final SimpMessagingTemplate template;
//
//    /**
//     * 클라이언트에서 메시지를 수신하는 엔드포인트
//     * - @MessageMapping 경로는 applicationDestinationPrefixes:'/app' 와 결합되어 '/app/chat.send/{clubId}' 가 됩니다.
//     * - @DestinationVariable 로 STOMP 경로 변수 clubId를 추출합니다.
//     * - @Payload 로 메시지 바디를 DTO로 매핑합니다.
//     *
//     * @param clubId  채팅방(모임) ID
//     * @param dto     전송된 메시지 DTO (userId, content 포함)
//     */
//    @MessageMapping("/chat.send/{clubId}")
//    public void sendMessage(
//        @DestinationVariable Integer clubId,
//        @Payload ChatMessageDTO dto
//    ) {
//        logger.info("Received WebSocket message for club {}: {}", clubId, dto);
//
//        // 1) 메시지 저장
//        ChatMessageEntity saved = messageService.sendMessage(clubId, dto.getUserId(), dto.getContent());
//
//        // 2) 저장된 메시지를 구독 채널로 브로드캐스트 (/topic/chat/{clubId})
//        template.convertAndSend(
//            "/topic/chat/" + clubId,
//            ChatMessageDTO.fromEntity(saved)
//        );
//    }
//}
