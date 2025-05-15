
package com.bookreum.dev.domain.club.dto;

import com.bookreum.dev.domain.club.entity.ChatRoomEntity;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomDTO {
    private Integer id;
    private Integer clubId;
    private String name;

    /**
     * 엔티티 → DTO 변환 (null 안전성 처리)
     */
    public static ChatRoomDTO fromEntity(ChatRoomEntity e) {
        if (e == null) {
            return null;
        }
        return ChatRoomDTO.builder()
            .id(e.getId())
            .clubId(e.getClub() != null ? e.getClub().getId() : null)
            .name(e.getName())
            .build();
    }
}
