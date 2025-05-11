package com.bookreum.dev.domain.club;

import static org.assertj.core.api.Assertions.assertThat;

import com.bookreum.dev.domain.club.entity.ChatRoomEntity;
import com.bookreum.dev.domain.club.entity.ClubEntity;
import com.bookreum.dev.domain.club.repository.ChatRoomRepository;
import com.bookreum.dev.domain.club.service.ChatRoomService;
import com.bookreum.dev.domain.club.service.ClubService;
import com.bookreum.dev.domain.user.UserEntity;
import com.bookreum.dev.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ChatRoomServiceTest {

    @Autowired
    private ChatRoomService chatRoomService; // ✅ 채팅방 서비스

    @Autowired
    private ClubService clubService; // ✅ 모임 서비스

    @Autowired
    private ChatRoomRepository chatRoomRepository; // ✅ 채팅방 저장소 (DB 직접 확인용)

    @Autowired
    private UserRepository userRepository; // ✅ 사용자 저장소

    // 테스트용 사용자 및 모임 (클럽)
    private UserEntity owner;
    private ClubEntity club;

    // ✅ 테스트 실행 전 초기화 (사용자 및 모임 생성)
    @BeforeEach
    void setUp() {
        System.out.println("=== Setting Up Test Environment ===");

        // ✅ 1) 사용자 생성 (모임 생성자)
        owner = userRepository.save(UserEntity.builder()
            .kakaoId("host-" + System.nanoTime()) // 고유한 ID 생성
            .nickname("host") // 사용자 닉네임
            .profileImage("http://example.com/host.png") // 프로필 이미지 URL
            .build()
        );
        System.out.println("Created Host ID: " + owner.getId());

        // ✅ 2) 모임 생성 (사용자를 호스트로 지정)
        club = clubService.createClub(ClubEntity.builder()
            .title("ChatRoom Test Club") // 모임 제목
            .description("채팅방 테스트") // 모임 설명
            .user(owner) // 모임 생성자
            .build()
        );
        System.out.println("Created Club ID: " + club.getId());
    }

    // ✅ 채팅방 생성 및 조회 테스트
    @Test
    void testCreateAndGetChatRoom() {
        System.out.println("=== Test: Create and Get Chat Room ===");

        // ✅ 1) 채팅방 생성
        ChatRoomEntity room = chatRoomService.createChatRoomForClub(club.getId());
        System.out.println("Created Chat Room ID: " + room.getId());

        // ✅ 2) 생성된 채팅방 검증 (Null 체크, ID 확인, 모임 확인)
        assertThat(room).isNotNull();
        assertThat(room.getId()).isNotNull();
        assertThat(room.getClub()).isEqualTo(club);

        // ✅ 3) 생성된 채팅방을 DB에서 직접 조회하여 확인
        ChatRoomEntity fromDb = chatRoomRepository.findById(room.getId()).orElse(null);
        System.out.println("Retrieved Chat Room from DB: " + (fromDb != null ? fromDb.getId() : "Not found"));
        assertThat(fromDb).isNotNull();
        assertThat(fromDb.getClub()).isEqualTo(club);

        // ✅ 4) 서비스 메서드를 통해 채팅방 조회
        ChatRoomEntity found = chatRoomService.getChatRoomByClub(club.getId());
        System.out.println("Retrieved Chat Room by Service: " + (found != null ? found.getId() : "Not found"));
        assertThat(found).isEqualTo(room);
    }

    // ✅ 중복 채팅방 생성 방지 테스트 (예외 발생)
    @Test
    void testDuplicateChatRoomCreation() {
        System.out.println("=== Test: Prevent Duplicate Chat Room Creation ===");

        // ✅ 1) 첫 번째 채팅방 생성
        chatRoomService.createChatRoomForClub(club.getId());

        // ✅ 2) 동일 클럽에 두 번째 채팅방 생성 시도 (예외 발생 예상)
        try {
            chatRoomService.createChatRoomForClub(club.getId());
        } catch (IllegalStateException e) {
            System.out.println("✅ Duplicate Chat Room Creation Blocked: " + e.getMessage());
            assertThat(e.getMessage()).contains("이미 채팅방이 존재합니다");
        }
    }

    // ✅ 채팅방 삭제 테스트
    @Test
    void testDeleteChatRoom() {
        System.out.println("=== Test: Delete Chat Room ===");

        // ✅ 1) 채팅방 생성
        ChatRoomEntity room = chatRoomService.createChatRoomForClub(club.getId());
        System.out.println("Created Chat Room ID: " + room.getId());

        // ✅ 2) 생성된 채팅방 삭제
        chatRoomService.deleteChatRoom(room.getId());
        System.out.println("Deleted Chat Room ID: " + room.getId());

        // ✅ 3) DB에서 삭제된 채팅방 확인 (존재하지 않아야 함)
        ChatRoomEntity fromDb = chatRoomRepository.findById(room.getId()).orElse(null);
        assertThat(fromDb).isNull();
        System.out.println("✅ Chat Room Successfully Deleted.");
    }
}
