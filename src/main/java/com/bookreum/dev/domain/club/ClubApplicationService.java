package com.bookreum.dev.domain.club;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookreum.dev.domain.user.UserEntity;

/**
 * 북클럽 신청 관련 비즈니스 로직을 처리하는 서비스
 */
@Service
public class ClubApplicationService {

    private final ClubApplicationRepository clubApplicationRepository;

    @Autowired
    public ClubApplicationService(ClubApplicationRepository clubApplicationRepository) {
        this.clubApplicationRepository = clubApplicationRepository;
    }

    /**
     * 사용자가 클럽에 신청합니다.
     * 중복 신청 방지를 위해 existsByClubAndUser를 먼저 확인합니다.
     */
    @Transactional
    public ClubApplication applyToClub(ClubEntity club, UserEntity user) {
        if (clubApplicationRepository.existsByClubAndUser(club, user)) {
            throw new IllegalStateException("이미 신청된 클럽입니다.");
        }
        return clubApplicationRepository.save(ClubApplication.builder()
            .club(club)
            .user(user)
            .build());
    }

    /**
     * 사용자의 클럽 신청을 취소합니다.
     */
    @Transactional
    public void cancelApplication(ClubEntity club, UserEntity user) {
        clubApplicationRepository.deleteByClubAndUser(club, user);
    }

    /**
     * 특정 클럽의 신청 목록을 조회합니다.
     */
    @Transactional(readOnly = true)
    public List<ClubApplication> getApplications(ClubEntity club) {
        return clubApplicationRepository.findByClub(club);
    }

    /**
     * 사용자가 특정 클럽에 신청했는지 여부를 반환합니다.
     */
    @Transactional(readOnly = true)
    public boolean isApplied(ClubEntity club, UserEntity user) {
        return clubApplicationRepository.existsByClubAndUser(club, user);
    }
}
