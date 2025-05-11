package com.bookreum.dev.domain.club.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookreum.dev.domain.club.entity.ClubApplicationEntity;
import com.bookreum.dev.domain.club.entity.ClubEntity;
import com.bookreum.dev.domain.club.repository.ClubApplicationRepository;
import com.bookreum.dev.domain.user.UserEntity;

/**
 * ✅ 북클럽 신청 관련 비즈니스 로직을 처리하는 서비스
 * - 중복 신청 방지, 최대 참가 인원 초과 방지 등
 */
@Service
public class ClubApplicationService {

    private final ClubApplicationRepository clubApplicationRepository;

    @Autowired
    public ClubApplicationService(ClubApplicationRepository clubApplicationRepository) {
        this.clubApplicationRepository = clubApplicationRepository;
    }

    /**
     * ✅ 사용자가 클럽에 신청합니다.
     * - 중복 신청 방지 (이미 신청된 클럽 확인)
     * - 최대 참가 인원 초과 방지 (최대 인원 확인)
     * 
     * @param club 신청할 클럽
     * @param user 신청하는 사용자
     * @return ClubApplicationEntity 생성된 신청 엔티티
     */
    @Transactional
    public ClubApplicationEntity applyToClub(ClubEntity club, UserEntity user) {
        validateDuplicateApplication(club, user);
        validateMaxParticipants(club);

        // ✅ 3) 신청 생성 및 저장
        return clubApplicationRepository.save(ClubApplicationEntity.builder()
            .club(club)
            .user(user)
            .build());
    }

    /**
     * ✅ 중복 신청 확인
     * - 이미 신청된 클럽일 경우 예외 발생
     * 
     * @param club 클럽
     * @param user 사용자
     */
    private void validateDuplicateApplication(ClubEntity club, UserEntity user) {
        if (clubApplicationRepository.existsByClubAndUser(club, user)) {
            throw new IllegalStateException("이미 신청된 클럽입니다.");
        }
    }

    /**
     * ✅ 최대 참가 인원 확인
     * - 최대 인원을 초과할 경우 예외 발생
     * 
     * @param club 클럽
     */
    private void validateMaxParticipants(ClubEntity club) {
        long currentApplications = clubApplicationRepository.countByClub(club);
        Integer maxParticipants = club.getMaxParticipants();

        if (maxParticipants != null && currentApplications >= maxParticipants) {
            throw new IllegalStateException(
                String.format("최대 참가 인원을 초과했습니다. (현재: %d / 최대: %d)", 
                currentApplications, maxParticipants)
            );
        }
    }

    /**
     * ✅ 사용자의 클럽 신청을 취소합니다.
     * 
     * @param club 클럽
     * @param user 사용자
     */
    @Transactional
    public void cancelApplication(ClubEntity club, UserEntity user) {
        clubApplicationRepository.deleteByClubAndUser(club, user);
    }

    /**
     * ✅ 특정 클럽의 신청 목록을 조회합니다.
     * 
     * @param club 클럽
     * @return List<ClubApplicationEntity> 신청 목록
     */
    @Transactional(readOnly = true)
    public List<ClubApplicationEntity> getApplications(ClubEntity club) {
        return clubApplicationRepository.findByClub(club);
    }

    /**
     * ✅ 사용자가 특정 클럽에 신청했는지 여부를 반환합니다.
     * 
     * @param club 클럽
     * @param user 사용자
     * @return boolean 신청 여부
     */
    @Transactional(readOnly = true)
    public boolean isApplied(ClubEntity club, UserEntity user) {
        return clubApplicationRepository.existsByClubAndUser(club, user);
    }
}
