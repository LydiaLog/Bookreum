package com.bookreum.dev.domain.club.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
	@Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 외부 브로커 사용 예시
        // config.enableStompBrokerRelay("/topic", "/queue")
        //       .setRelayHost("rabbitmq.myhost.com")
        //       .setRelayPort(61613)
        //       .setClientLogin("guest")
        //       .setClientPasscode("guest");
        
        // 가벼운 로컬 브로커
        config.enableSimpleBroker("/topic", "/queue");
        
        // @MessageMapping 시작용 prefix
        config.setApplicationDestinationPrefixes("/app");
        
        // 개인 메시지용 prefix
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-chat")
                .setAllowedOrigins("*") // 모든 로컬 클라이언트 허용 -> 배포시 해당 도메인으로 수정
                .withSockJS();
               
    }
}
