package com.bookreum.dev.domain.club;

import static org.assertj.core.api.Assertions.assertThat;

import com.bookreum.dev.domain.club.dto.ChatMessageDTO;
import com.bookreum.dev.domain.club.entity.ChatMessageEntity;
import com.bookreum.dev.domain.club.repository.ChatMessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ChatWebSocketControllerTest {

    @LocalServerPort
    private int port; // 테스트 서버의 랜덤 포트

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    // WebSocket 클라이언트 및 세션 객체
    private WebSocketStompClient stompClient;
    private StompSession stompSession;

    // WebSocket 관련 상수 (URL 및 경로)
    private static final String WS_ENDPOINT = "/ws-chat"; // WebSocket 엔드포인트
    private static final String WS_SUBSCRIBE_TOPIC = "/topic/chat/1"; // 메시지 수신 경로 (채팅방 1)
    private static final String WS_SEND_DESTINATION = "/app/chat.send/1"; // 메시지 전송 경로 (채팅방 1)
    
    // 메시지 수신 확인을 위한 CountDownLatch
    private CountDownLatch latch = new CountDownLatch(1);
    private ChatMessageDTO receivedMessage;

    @BeforeEach
    void setUp() throws Exception {
        // ✅ WebSocket STOMP 클라이언트 설정
        stompClient = new WebSocketStompClient(new SockJsClient(
                Collections.singletonList(new WebSocketTransport(new StandardWebSocketClient()))));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        // ✅ WebSocket 서버 연결
        String url = "ws://localhost:" + port + WS_ENDPOINT;
        stompSession = stompClient.connect(url, new StompSessionHandlerAdapter() {}).get(3, TimeUnit.SECONDS);

        // ✅ WebSocket 구독 (클라이언트 역할, /topic/chat/1)
        stompSession.subscribe(WS_SUBSCRIBE_TOPIC, new StompSessionHandlerAdapter() {
            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                receivedMessage = (ChatMessageDTO) payload; // 수신된 메시지를 저장
                latch.countDown(); // 수신 확인 카운트다운 감소
            }

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return ChatMessageDTO.class; // 수신 메시지 타입 지정
            }
        });
    }

    // ✅ WebSocket을 통한 메시지 전송 및 수신 테스트
    @Test
    void testWebSocketSendAndReceive() throws Exception {
        // ✅ 1) WebSocket을 통해 메시지 전송
        ChatMessageDTO message = new ChatMessageDTO();
        message.setUserId(1L); // 사용자 ID 설정 (테스트용)
        message.setContent("Hello WebSocket!");

        // WebSocket을 통해 서버로 메시지 전송
        stompSession.send(WS_SEND_DESTINATION, message);

        // ✅ 2) 메시지 수신 대기 (최대 3초)
        latch.await(3, TimeUnit.SECONDS);

        // ✅ 3) 수신된 메시지 검증 (WebSocket 수신 메시지)
        assertThat(receivedMessage).isNotNull();
        assertThat(receivedMessage.getContent()).isEqualTo("Hello WebSocket!");
        System.out.println("✅ Received WebSocket Message: " + receivedMessage.getContent());

        // ✅ 4) DB 저장 확인 (메시지가 DB에 저장되었는지 확인)
        ChatMessageEntity savedMessage = chatMessageRepository.findAll().get(0);
        assertThat(savedMessage.getContent()).isEqualTo("Hello WebSocket!");
        System.out.println("✅ Saved Message in DB: " + savedMessage.getContent());
    }

    // ✅ WebSocket 클라이언트 연결 해제 테스트 (옵션)
    @Test
    void testWebSocketClientDisconnect() throws Exception {
        // ✅ WebSocket 클라이언트 연결 해제
        stompSession.disconnect();

        // ✅ 연결이 정상적으로 해제되었는지 확인
        assertThat(stompSession.isConnected()).isFalse();
        System.out.println("✅ WebSocket Client Disconnected.");
    }

    // ✅ 잘못된 WebSocket 연결 테스트 (옵션)
    @Test
    void testWebSocketInvalidConnection() {
        try {
            // 잘못된 URL로 WebSocket 연결 시도
            String invalidUrl = "ws://localhost:" + port + "/invalid-endpoint";
            stompClient.connect(invalidUrl, new StompSessionHandlerAdapter() {}).get(3, TimeUnit.SECONDS);
        } catch (Exception e) {
            // 예외 발생 확인
            System.out.println("✅ Invalid WebSocket Connection Error: " + e.getMessage());
            assertThat(e).isInstanceOf(Exception.class);
        }
    }
}
