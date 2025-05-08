package com.bookreum.dev.domain.club;

import static org.assertj.core.api.Assertions.assertThat;

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
class ChatRoomServiceTest {

    @Autowired
    private ChatRoomService chatRoomService;

    @Autowired
    private ClubService clubService;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private UserRepository userRepository;

    private UserEntity owner;
    private ClubEntity club;

    @BeforeEach
    void setUp() {
        // 1) 모임 생성자(호스트) 저장
        owner = userRepository.save(UserEntity.builder()
            .kakaoId("host-" + System.nanoTime())
            .nickname("host")
            .profileImage("http://example.com/host.png")
            .build()
        );
        // 2) 해당 호스트로 모임 생성
        club = clubService.createClub(ClubEntity.builder()
            .title("ChatRoom Test Club")
            .description("채팅방 테스트")
            .user(owner)
            .build()
        );
    }

    @Test
    void testCreateAndGetChatRoom() {
        // — 채팅방 생성
        ChatRoomEntity room = chatRoomService.createChatRoomForClub(club.getId());
        assertThat(room.getId()).isNotNull();
        assertThat(room.getClub()).isEqualTo(club);

        // — 생성된 채팅방 조회
        ChatRoomEntity found = chatRoomService.getChatRoomByClub(club.getId());
        assertThat(found).isEqualTo(room);
    }
}
