//package com.bookreum.dev.domain.club.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.messaging.simp.config.MessageBrokerRegistry;
//import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
//import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
//import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
//import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
//import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
//import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;
//import org.springframework.messaging.simp.config.ChannelRegistration;
//import org.springframework.messaging.support.ChannelInterceptor;
//import org.springframework.messaging.Message;
//import org.springframework.messaging.MessageChannel;
//import org.springframework.messaging.simp.stomp.StompCommand;
//import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
//import org.springframework.messaging.support.MessageHeaderAccessor;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//@Configuration
//@EnableWebSocketMessageBroker
//@RequiredArgsConstructor
//public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
//
//    private final UserDetailsService userDetailsService;
//
//    @Override
//    public void configureMessageBroker(MessageBrokerRegistry config) {
//        config.enableSimpleBroker("/topic");
//        config.setApplicationDestinationPrefixes("/app");
//    }
//
//    @Override
//    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        registry.addEndpoint("/ws-chat")
//                .setAllowedOrigins("http://localhost:5173", "http://127.0.0.1:5173","http://10.50.216.86:5173")
//                .addInterceptors(new HttpSessionHandshakeInterceptor())
//                .setHandshakeHandler(new DefaultHandshakeHandler())
//                .withSockJS();
//    }
//
//    @Override
//    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
//        registration.setMessageSizeLimit(8192) // 메시지 크기 제한
//                   .setSendBufferSizeLimit(512 * 1024) // 전송 버퍼 크기
//                   .setSendTimeLimit(20000); // 전송 타임아웃
//    }
//
//    @Override
//    public void configureClientInboundChannel(ChannelRegistration registration) {
//        registration.interceptors(new ChannelInterceptor() {
//            @Override
//            public Message<?> preSend(Message<?> message, MessageChannel channel) {
//                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
//                
//                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
//                    String token = accessor.getFirstNativeHeader("Authorization");
//                    log.info("WebSocket connection attempt with token: {}", token);
//                    
//                    if (token != null && token.startsWith("Bearer ")) {
//                        token = token.substring(7);
//                        try {
//                            // 토큰에서 사용자 정보 추출
//                            String kakaoId = accessor.getFirstNativeHeader("kakaoId");
//                            if (kakaoId != null) {
//                                UserDetails userDetails = userDetailsService.loadUserByUsername(kakaoId);
//                                UsernamePasswordAuthenticationToken auth = 
//                                    new UsernamePasswordAuthenticationToken(
//                                        userDetails,
//                                        null,
//                                        userDetails.getAuthorities()
//                                    );
//                                accessor.setUser(auth);
//                                log.info("WebSocket connection authenticated for user: {}", kakaoId);
//                            }
//                        } catch (Exception e) {
//                            log.error("WebSocket authentication failed: {}", e.getMessage());
//                            return null;
//                        }
//                    }
//                }
//                return message;
//            }
//        });
//    }
//}