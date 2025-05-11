package com.bookreum.dev.domain.club;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import com.bookreum.dev.domain.club.entity.ClubApplicationEntity;
import com.bookreum.dev.domain.club.entity.ClubEntity;
import com.bookreum.dev.domain.club.repository.ClubApplicationRepository;
import com.bookreum.dev.domain.club.service.ClubApplicationService;
import com.bookreum.dev.domain.club.service.ClubService;
import com.bookreum.dev.domain.user.UserEntity;
import com.bookreum.dev.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * ✅ ClubApplicationService 테스트 클래스
 * - 신청, 취소, 중복 신청, 최대 참가 인원 초과 등 다양한 상황 테스트
 */
@SpringBootTest
class ClubApplicationServiceTest {

    @Autowired
    private ClubApplicationService applicationService;

    @Autowired
    private ClubService clubService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClubApplicationRepository appRepository;

    private UserEntity user;
    private ClubEntity club;

    /**
     * ✅ 테스트 환경 설정 (사용자, 클럽 생성)
     */
    @BeforeEach
    void setUp() {
        System.out.println("=== Setting Up Test Environment ===");

        // ✅ 사용자 생성
        user = userRepository.save(UserEntity.builder()
            .kakaoId("applicant-" + System.nanoTime())
            .nickname("applicant")
            .profileImage("http://example.com/applicant.png")
            .build()
        );
        System.out.println("Created User ID: " + user.getId());

        // ✅ 모임 생성 (신청자와 동일인으로 생성자 설정)
        club = clubService.createClub(ClubEntity.builder()
            .title("Application Test Club")
            .description("테스트 클럽")
            .user(user)
            .maxParticipants(5) // 기본 최대 참가 인원 설정
            .build()
        );
        System.out.println("Created Club ID: " + club.getId());
    }

    /**
     * ✅ 모임 신청, 취소, 상태 확인 테스트
     */
    @Test
    void testApplyCancelAndCheck() {
        System.out.println("=== Test: Apply, Cancel, and Check ===");

        // ✅ 신청 전: false 이어야 함
        boolean isAppliedBefore = applicationService.isApplied(club, user);
        assertThat(isAppliedBefore).isFalse();

        // ✅ 신청
        ClubApplicationEntity app = applicationService.applyToClub(club, user);
        assertThat(app).isNotNull();
        assertThat(app.getUser().getId()).isEqualTo(user.getId());

        // ✅ 신청 후: true
        boolean isAppliedAfter = applicationService.isApplied(club, user);
        assertThat(isAppliedAfter).isTrue();

        // ✅ 신청 목록 확인
        List<ClubApplicationEntity> list = applicationService.getApplications(club);
        assertThat(list).contains(app);

        // ✅ DB에서 직접 확인
        ClubApplicationEntity fromDb = appRepository.findById(app.getId()).orElse(null);
        assertThat(fromDb).isNotNull();

        // ✅ 신청 취소
        applicationService.cancelApplication(club, user);
        boolean isAppliedAfterCancel = applicationService.isApplied(club, user);
        assertThat(isAppliedAfterCancel).isFalse();

        // ✅ DB에서 직접 조회하여 신청 취소 확인
        List<ClubApplicationEntity> updatedList = applicationService.getApplications(club);
        assertThat(updatedList).doesNotContain(app);
    }

    /**
     * ✅ 최대 참가 인원 초과 테스트
     */
    @Test
    void testMaxParticipantsExceeded() {
        System.out.println("=== Test: Max Participants Exceeded ===");

        // ✅ 클럽 설정 (최대 참가 인원 2명)
        club.setMaxParticipants(2);
        clubService.createClub(club);

        // ✅ 첫 번째 사용자 신청
        applicationService.applyToClub(club, user);

        // ✅ 두 번째 사용자 신청
        UserEntity secondUser = createNewUser("applicant2");
        applicationService.applyToClub(club, secondUser);

        // ✅ 세 번째 사용자 신청 (최대 참가 인원 초과)
        UserEntity thirdUser = createNewUser("applicant3");

        // ✅ 예외 발생 확인
        assertThatThrownBy(() -> applicationService.applyToClub(club, thirdUser))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("최대 참가 인원을 초과했습니다.");
    }

    /**
     * ✅ 중복 신청 방지 테스트
     */
    @Test
    void testDuplicateApplication() {
        System.out.println("=== Test: Duplicate Application ===");

        // ✅ 첫 번째 신청 성공
        applicationService.applyToClub(club, user);

        // ✅ 중복 신청 시 예외 발생 확인
        assertThatThrownBy(() -> applicationService.applyToClub(club, user))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("이미 신청된 클럽입니다.");
    }

    /**
     * ✅ 클럽에 대한 신청 목록 조회 테스트
     */
    @Test
    void testGetApplicationList() {
        System.out.println("=== Test: Get Application List ===");

        // ✅ 다수 사용자 신청
        applicationService.applyToClub(club, user);
        applicationService.applyToClub(club, createNewUser("applicant2"));
        applicationService.applyToClub(club, createNewUser("applicant3"));

        // ✅ 신청 목록 확인
        List<ClubApplicationEntity> list = applicationService.getApplications(club);
        assertThat(list).hasSize(3);
    }

    /**
     * ✅ 새로운 사용자 생성 메서드 (중복 코드 제거)
     * @param nickname 사용자 닉네임
     * @return UserEntity 생성된 사용자
     */
    private UserEntity createNewUser(String nickname) {
        return userRepository.save(UserEntity.builder()
            .kakaoId(nickname + "-" + System.nanoTime())
            .nickname(nickname)
            .profileImage("http://example.com/" + nickname + ".png")
            .build()
        );
    }
}
