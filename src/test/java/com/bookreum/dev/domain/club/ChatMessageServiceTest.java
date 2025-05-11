package com.bookreum.dev.domain.club;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import com.bookreum.dev.domain.club.entity.ChatMessageEntity;
import com.bookreum.dev.domain.club.entity.ChatRoomEntity;
import com.bookreum.dev.domain.club.entity.ClubEntity;
import com.bookreum.dev.domain.club.service.ChatMessageService;
import com.bookreum.dev.domain.club.service.ChatRoomService;
import com.bookreum.dev.domain.club.service.ClubService;
import com.bookreum.dev.domain.user.UserEntity;
import com.bookreum.dev.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ChatMessageServiceTest {

    @Autowired
    private ChatMessageService messageService;

    @Autowired
    private ChatRoomService chatRoomService;

    @Autowired
    private ClubService clubService;

    @Autowired
    private UserRepository userRepository;

    private UserEntity sender;
    private ClubEntity club;
    private ChatRoomEntity room;

    @BeforeEach
    void setUp() {
        sender = userRepository.save(UserEntity.builder()
            .kakaoId("sender-" + System.nanoTime())
            .nickname("sender")
            .profileImage("http://example.com/sender.png")
            .build()
        );

        club = clubService.createClub(ClubEntity.builder()
            .title("Message Test Club")
            .description("메시지 테스트")
            .user(sender)
            .build()
        );

        room = chatRoomService.createChatRoomForClub(club.getId());
    }

    // ✅ 메시지 전송 및 조회 테스트 (DB 직접 반영)
    @Test
    void testSendAndGetMessages() {
        ChatMessageEntity msg = messageService.sendMessage(club.getId(), sender.getId(), "Hello World");
        assertThat(msg).isNotNull();
        assertThat(msg.getContent()).isEqualTo("Hello World");

        List<ChatMessageEntity> messages = messageService.getMessages(club.getId(), 0, 10);
        assertThat(messages).hasSize(1);
        assertThat(messages.get(0).getContent()).isEqualTo("Hello World");
    }

    // ✅ 페이징 메시지 조회 테스트
    @Test
    void testGetMessagesWithPaging() {
        for (int i = 1; i <= 10; i++) {
            messageService.sendMessage(club.getId(), sender.getId(), "Paged Message " + i);
        }

        List<ChatMessageEntity> page1 = messageService.getMessages(club.getId(), 0, 5);
        assertThat(page1).hasSize(5);
        assertThat(page1.get(0).getContent()).isEqualTo("Paged Message 1");

        List<ChatMessageEntity> page2 = messageService.getMessages(club.getId(), 1, 5);
        assertThat(page2).hasSize(5);
        assertThat(page2.get(0).getContent()).isEqualTo("Paged Message 6");
    }

    // ✅ 메시지 삭제 (ID로) 테스트
    @Test
    void testDeleteMessageById() {
        ChatMessageEntity msg = messageService.sendMessage(club.getId(), sender.getId(), "Message to Delete");
        messageService.deleteMessage(msg.getId());

        List<ChatMessageEntity> messages = messageService.getMessages(club.getId(), 0, 10);
        assertThat(messages).doesNotContain(msg);
    }

    // ✅ 모든 메시지 삭제 (클럽에서) 테스트
    @Test
    void testDeleteAllMessagesInRoom() {
        for (int i = 1; i <= 5; i++) {
            messageService.sendMessage(club.getId(), sender.getId(), "Message " + i);
        }

        messageService.deleteAllMessagesInRoom(club.getId());
        long messageCount = messageService.getMessageCount(club.getId());
        assertThat(messageCount).isEqualTo(0);
    }

    // ✅ 예외 처리 테스트 (존재하지 않는 클럽)
    @Test
    void testSendMessageToNonExistentClub() {
        assertThatThrownBy(() -> messageService.sendMessage(999L, sender.getId(), "Invalid Club"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("존재하지 않는 모임 ID");
    }

    // ✅ 예외 처리 테스트 (존재하지 않는 사용자)
    @Test
    void testSendMessageByNonExistentUser() {
        assertThatThrownBy(() -> messageService.sendMessage(club.getId(), 999L, "Invalid User"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("존재하지 않는 사용자 ID");
    }

    // ✅ 공백 메시지 전송 예외 테스트
    @Test
    void testSendEmptyMessage() {
        assertThatThrownBy(() -> messageService.sendMessage(club.getId(), sender.getId(), ""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("메시지 내용은 비어있을 수 없습니다.");
    }
}
