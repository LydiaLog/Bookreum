package com.bookreum.dev.domain.club.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookreum.dev.domain.club.entity.ClubApplicationEntity;
import com.bookreum.dev.domain.club.entity.ClubEntity;
import com.bookreum.dev.domain.club.entity.ClubStatus;
import com.bookreum.dev.domain.club.repository.ClubApplicationRepository;
import com.bookreum.dev.domain.user.UserEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClubApplicationService {
    private final ClubApplicationRepository applicationRepository;

    /** 모임 신청 */
    @Transactional
    public ClubApplicationEntity applyToClub(ClubEntity club, UserEntity user) {
        // applications 컬렉션을 초기화하기 위해 countByClub 사용
    	long count = applicationRepository.countByClub(club);
    	club.updateStatus((int) count);
        if (club.getMaxParticipants() != null && count >= club.getMaxParticipants()) {
            throw new IllegalStateException(
                String.format("최대 참가 인원을 초과했습니다. (현재 %d / 최대 %d)", count, club.getMaxParticipants())
            );
        }
        
        club.updateStatus((int) count);
        if (club.getStatus() == ClubStatus.CLOSED) {
            throw new IllegalStateException("모집이 마감되었습니다.");
        }
        if (applicationRepository.existsByClubAndUser(club, user)) {
            throw new IllegalStateException("이미 신청된 클럽입니다.");
        }
        
        return applicationRepository.save(
            ClubApplicationEntity.builder().club(club).user(user).build()
        );
    }
    /** 모임 신청 취소 */
    @Transactional
    public void cancelApplication(ClubEntity club, UserEntity user) {
        applicationRepository.deleteByClubAndUser(club, user);
    }

    /** 신청 여부 확인 */
    @Transactional(readOnly = true)
    public boolean isApplied(ClubEntity club, UserEntity user) {
        return applicationRepository.existsByClubAndUser(club, user);
    }
    /** 특정 모임의 모든 신청 내역 조회 */
    @Transactional(readOnly = true)
    public List<ClubApplicationEntity> getApplications(ClubEntity club) {
        return applicationRepository.findByClub(club);
    }
}