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
@Rollback(false) // 테스트 종료 후에도 트랜잭션을 롤백하지 않음 → 실제 DB에 반영 확인
class ClubServiceTest {

    @Autowired
    private ClubService clubService;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private UserRepository userRepository;

    private UserEntity owner;

    @BeforeEach
    void setUp() {
        // 테스트용 사용자 생성 및 저장
        owner = userRepository.save(UserEntity.builder()
            .kakaoId("owner-" + System.nanoTime())
            .nickname("owner")
            .profileImage("http://example.com/owner.png")
            .build()
        );
    }

    @Test
    void testCreateGetListDeleteClub() {
        // — 1) 모임 생성
        ClubEntity club = ClubEntity.builder()
            .title("Integration Test Club")
            .description("Integration Test Description")
            .user(owner)
            .build();
        ClubEntity saved = clubService.createClub(club);
        assertThat(saved.getId()).isNotNull(); // ID가 자동 생성되어야 함

        // — 2) 생성된 모임 조회
        ClubEntity found = clubService.getClub(saved.getId());
        assertThat(found.getTitle()).isEqualTo("Integration Test Club");
        assertThat(found.getUser()).isEqualTo(owner);

        // — 3) 전체 목록에 포함되는지 확인
        List<ClubEntity> all = clubService.listClubs();
        assertThat(all).contains(saved);

        // — 4) 모임 삭제
        clubService.deleteClub(saved.getId());
        assertThat(clubRepository.existsById(saved.getId())).isFalse();
    }
}
