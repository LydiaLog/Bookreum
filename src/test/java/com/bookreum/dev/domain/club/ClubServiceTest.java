package com.bookreum.dev.domain.club;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDateTime;
import java.util.List;

import com.bookreum.dev.domain.club.entity.ClubEntity;
import com.bookreum.dev.domain.club.entity.ClubStatus;
import com.bookreum.dev.domain.club.repository.ClubRepository;
import com.bookreum.dev.domain.club.service.ClubService;
import com.bookreum.dev.domain.user.UserEntity;
import com.bookreum.dev.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ClubServiceTest {

    @Autowired
    private ClubService clubService;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private UserRepository userRepository;

    // ✅ 테스트에 사용할 사용자 및 클럽 객체
    private UserEntity owner;
    private ClubEntity persistedClub;   // 조회 테스트용
    private ClubEntity clubToDelete;    // 삭제 테스트용
    private ClubEntity originalClub;    // 수정 테스트용

    /**
     * ✅ 각 테스트 실행 전 환경 설정
     */
    @BeforeEach
    void setUp() {
        System.out.println("=== Setting Up Test Environment ===");

        // ✅ 1) 테스트용 사용자 생성
        owner = userRepository.save(UserEntity.builder()
            .kakaoId("owner-" + System.nanoTime())
            .nickname("owner1")
            .profileImage("http://example.com/owner.png")
            .build()
        );
        System.out.println("Created Owner ID: " + owner.getId());

        // ✅ 2) 조회 테스트용 클럽
        persistedClub = clubService.createClub(ClubEntity.builder()
            .title("Retrieval Test Club")
            .description("조회 테스트")
            .user(owner)
            .build()
        );

        // ✅ 3) 목록 조회 테스트용 클럽들
        clubService.createClub(ClubEntity.builder()
            .title("List Club A")
            .description("목록 테스트 A")
            .user(owner)
            .build()
        );
        clubService.createClub(ClubEntity.builder()
            .title("List Club B")
            .description("목록 테스트 B")
            .user(owner)
            .build()
        );

        // ✅ 4) 삭제 테스트용 클럽
        clubToDelete = clubService.createClub(ClubEntity.builder()
            .title("ToDelete Club")
            .description("삭제 테스트")
            .user(owner)
            .build()
        );

        // ✅ 5) 수정 테스트용 클럽
        originalClub = clubService.createClub(ClubEntity.builder()
            .title("Original Club")
            .description("수정 전 클럽")
            .minParticipants(3)
            .maxParticipants(5)
            .applicationDeadline(LocalDateTime.now().plusDays(2))
            .activityDurationDays(14)
            .status(ClubStatus.OPEN)
            .user(owner)
            .build()
        );

        System.out.println("Initial Setup Completed.");
    }

    /**
     * ✅ 모임 생성 테스트
     */
    @Test
    void testCreateClub() {
        System.out.println("=== Test: Create Club ===");

        ClubEntity club = ClubEntity.builder()
            .title("Creation Test Club")
            .description("생성 테스트1")
            .user(owner)
            .build();

        // ✅ 클럽 생성
        ClubEntity saved = clubService.createClub(club);
        System.out.println("Saved Club ID: " + (saved != null ? saved.getId() : "null"));

        // ✅ 실제 DB에서 생성 확인
        ClubEntity fromDb = clubRepository.findById(saved.getId()).orElse(null);
        System.out.println("Retrieved from DB: " + (fromDb != null ? fromDb.getTitle() : "Not found"));

        // ✅ 생성된 클럽 검증
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUser().getId()).isEqualTo(owner.getId());
    }

    /**
     * ✅ 모임 조회 테스트
     */
    @Test
    void testGetClubById() {
        System.out.println("=== Test: Get Club By ID ===");

        // ✅ 클럽 조회
        ClubEntity found = clubService.getClub(persistedClub.getId());
        System.out.println("Found Club: " + (found != null ? found.getTitle() : "Not found"));

        // ✅ 조회된 클럽 검증
        assertThat(found).isNotNull();
        assertThat(found.getTitle()).isEqualTo("Retrieval Test Club");
        assertThat(found.getUser().getId()).isEqualTo(owner.getId());
    }

    /**
     * ✅ 모임 목록 조회 테스트
     */
    @Test
    void testListClubs() {
        System.out.println("=== Test: List Clubs ===");

        // ✅ 클럽 목록 조회
        List<ClubEntity> clubs = clubService.listClubs();
        System.out.println("Total Clubs: " + clubs.size());

        // ✅ 목록에 클럽이 포함되어 있는지 확인
        assertThat(clubs).extracting(ClubEntity::getTitle)
                         .contains("List Club A", "List Club B");
    }

    /**
     * ✅ 모임 삭제 테스트
     */
    @Test
    void testDeleteClub() {
        System.out.println("=== Test: Delete Club ===");

        // ✅ 클럽 삭제
        clubService.deleteClub(clubToDelete.getId());
        boolean exists = clubRepository.existsById(clubToDelete.getId());
        System.out.println("Club Exists After Deletion: " + exists);

        // ✅ 삭제 확인
        assertThat(exists).isFalse();
    }

    /**
     * ✅ 모임 수정 테스트
     */
    @Test
    void testUpdateClub() {
        System.out.println("=== Test: Update Club ===");

        // ✅ 수정할 클럽 정보
        ClubEntity changes = ClubEntity.builder()
            .title("Updated Club Title")
            .description("Updated Description")
            .minParticipants(4)
            .maxParticipants(8)
            .applicationDeadline(LocalDateTime.now().plusDays(5))
            .activityDurationDays(30)
            .status(ClubStatus.CLOSED)
            .build();

        // ✅ 클럽 수정
        ClubEntity updated = clubService.updateClub(originalClub.getId(), changes);
        System.out.println("Updated Club: " + (updated != null ? updated.getTitle() : "Not found"));

        // ✅ 수정된 내용 검증
        assertThat(updated).isNotNull();
        assertThat(updated.getTitle()).isEqualTo("Updated Club Title");

        // ✅ 실제 DB 반영 확인
        ClubEntity fromDb = clubRepository.findById(originalClub.getId()).orElseThrow();
        assertThat(fromDb.getTitle()).isEqualTo("Updated Club Title");
        assertThat(fromDb.getApplicationDeadline()).isEqualTo(changes.getApplicationDeadline());
    }
}
