// com/bookreum/dev/domain/club/dto/ChatMessageDTO.java
package com.bookreum.dev.domain.club;

import java.time.LocalDateTime;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDTO {
    private Long id;
    private Long chatRoomId;
    private Long userId;
    private String content;
    private LocalDateTime sentAt;

    public static ChatMessageDTO fromEntity(ChatMessageEntity e) {
        return ChatMessageDTO.builder()
            .id(e.getId())
            .chatRoomId(e.getChatRoom().getId())
            .userId(e.getUser().getId())
            .content(e.getContent())
            .sentAt(e.getSentAt())
            .build();
    }
}
