package com.bookreum.dev.domain.club.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;

/**
 * 북클럽 채팅방 엔티티
 */
@Entity
@Table(name = "chat_room")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
public class ChatRoomEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    /** 이 채팅방이 속한 모임 */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", unique = true, nullable = false)
    private ClubEntity club;
    
    //채팅방 엔티티 조회해서 전체 대화 불러오기
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ChatMessageEntity> messages = new ArrayList<>();

}
