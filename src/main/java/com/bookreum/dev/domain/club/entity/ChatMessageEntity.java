//package com.bookreum.dev.domain.club.entity;
//
//import java.time.LocalDateTime;
//
//import com.bookreum.dev.domain.user.UserEntity;
//import jakarta.persistence.*;
//import lombok.*;
//
///**
// * 북클럽 채팅 메시지 엔티티
// */
//@Entity
//@Table(name = "chat_message")
//@Getter @Setter
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@AllArgsConstructor
//@Builder(toBuilder = true)
//public class ChatMessageEntity {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Integer id;
//
//    /** 어떤 채팅방의 메시지인지 */
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "chat_room_id", nullable = false)
//    private ChatRoomEntity chatRoom;
//
//    /** 보낸 사람 */
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", nullable = false)
//    private UserEntity user;
//
//    /** 실제 내용 */
//    @Column(nullable = false)
//    private String content;
//
//    /** 보낸 시각 */
//    @Column(name = "sent_at", nullable = false, updatable = false)
//    private LocalDateTime sentAt;
//    
//    @PrePersist
//    private void onCreate() {
//        this.sentAt = LocalDateTime.now();
//    }
//}
