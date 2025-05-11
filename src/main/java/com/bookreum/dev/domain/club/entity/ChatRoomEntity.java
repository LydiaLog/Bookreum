// com/bookreum/dev/domain/club/ChatRoomEntity.java
package com.bookreum.dev.domain.club.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chat_room")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ChatRoomEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 채팅방 이름(예: 모임 제목 + " 채팅방") */
    private String name;

    /** 이 채팅방이 속한 모임 */
    @OneToOne @JoinColumn(name = "club_id", unique = true)
    private ClubEntity club;
}
