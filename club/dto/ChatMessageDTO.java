//package com.bookreum.dev.domain.club.dto;
//
//import java.time.LocalDateTime;
//import com.bookreum.dev.domain.club.entity.ChatMessageEntity;
//import lombok.*;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.NotNull;
//
//@Data
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//public class ChatMessageDTO {
//    
//    private Integer id;
//
//    @NotNull(message = "ChatRoom ID cannot be null")
//    private Integer chatRoomId;
//
//    @NotNull(message = "User ID cannot be null")
//    private Integer userId;
//
//    @NotBlank(message = "Message content cannot be empty")
//    private String content;
//
//    private LocalDateTime sentAt;
//
//    /**
//     * 엔티티에서 DTO로 변환 (안전성 추가)
//     */
//    public static ChatMessageDTO fromEntity(ChatMessageEntity e) {
//        if (e == null) {
//            throw new IllegalArgumentException("ChatMessageEntity cannot be null");
//        }
//
//        return ChatMessageDTO.builder()
//            .id(e.getId())
//            .chatRoomId(e.getChatRoom() != null ? e.getChatRoom().getId() : null)
//            .userId(e.getUser() != null ? e.getUser().getId() : null)
//            .content(e.getContent())
//            .sentAt(e.getSentAt())
//            .build();
//    }
//}
