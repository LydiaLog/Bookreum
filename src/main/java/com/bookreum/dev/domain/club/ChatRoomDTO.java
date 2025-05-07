// com/bookreum/dev/domain/club/dto/ChatRoomDTO.java
package com.bookreum.dev.domain.club;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomDTO {
    private Long id;
    private Long clubId;
    private String name;

    public static ChatRoomDTO fromEntity(ChatRoomEntity e) {
        return ChatRoomDTO.builder()
            .id(e.getId())
            .clubId(e.getClub().getId())
            .name(e.getName())
            .build();
    }
}
