package com.bookreum.dev.domain.club;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import com.bookreum.dev.domain.user.UserEntity;
import com.bookreum.dev.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Rollback(false)
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
        // 1) 메시지 발신자 저장
        sender = userRepository.save(UserEntity.builder()
            .kakaoId("sender-" + System.nanoTime())
            .nickname("sender")
            .profileImage("http://example.com/sender.png")
            .build()
        );
        // 2) 모임 생성
        club = clubService.createClub(ClubEntity.builder()
            .title("Message Test Club")
            .description("메시지 테스트")
            .user(sender)
            .build()
        );
        // 3) 해당 모임의 채팅방 생성
        room = chatRoomService.createChatRoomForClub(club.getId());
    }

    @Test
    void testSendAndGetMessages() {
        // — 메시지 전송
        ChatMessageEntity msg = messageService.sendMessage(club.getId(), sender.getId(), "Hello Integration");
        assertThat(msg.getId()).isNotNull();              // ID 자동 생성 확인
        assertThat(msg.getContent()).isEqualTo("Hello Integration");
        assertThat(msg.getSentAt()).isNotNull();          // 전송 시각 자동 설정 확인

        // — 채팅방 메시지 목록 조회
        List<ChatMessageEntity> msgs = messageService.getMessages(club.getId());
        assertThat(msgs).contains(msg);
    }
}
