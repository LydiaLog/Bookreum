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

    @BeforeEach
    void setUp() {
        // 1) 사용자 생성
        user = userRepository.save(UserEntity.builder()
            .kakaoId("applicant-" + System.nanoTime())
            .nickname("applicant")
            .profileImage("http://example.com/applicant.png")
            .build()
        );
        // 2) 모임 생성 (신청자와 동일인으로 생성자 설정)
        club = clubService.createClub(ClubEntity.builder()
            .title("Application Test Club")
            .description("테스트 클럽")
            .user(user)
            .build()
        );
    }

    @Test
    void testApplyCancelAndCheck() {
        // — 신청 전: false 이어야 함
        assertThat(applicationService.isApplied(club, user)).isFalse();

        // — 신청
        ClubApplication app = applicationService.applyToClub(club, user);
        assertThat(app.getAppliedAt()).isNotNull(); // appliedAt 자동 설정 확인

        // — 신청 후: true
        assertThat(applicationService.isApplied(club, user)).isTrue();
        List<ClubApplication> list = applicationService.getApplications(club);
        assertThat(list).contains(app);

        // — 신청 취소
        applicationService.cancelApplication(club, user);
        assertThat(applicationService.isApplied(club, user)).isFalse();
    }
}
