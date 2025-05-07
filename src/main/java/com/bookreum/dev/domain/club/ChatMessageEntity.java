// com/bookreum/dev/domain/club/ChatMessageEntity.java
package com.bookreum.dev.domain.club;

import java.time.LocalDateTime;
import com.bookreum.dev.domain.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chat_message")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ChatMessageEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 어떤 채팅방의 메시지인지 */
    @ManyToOne @JoinColumn(name = "chat_room_id")
    private ChatRoomEntity chatRoom;

    /** 보낸 사람 */
    @ManyToOne @JoinColumn(name = "user_id")
    private UserEntity user;

    /** 실제 내용 */
    private String content;

    /** 보낸 시각 */
    private LocalDateTime sentAt = LocalDateTime.now();
}
