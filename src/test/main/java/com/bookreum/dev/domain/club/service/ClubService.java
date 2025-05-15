package com.bookreum.dev.domain.club.service;

import com.bookreum.dev.domain.club.entity.ClubEntity;
import com.bookreum.dev.domain.club.repository.ClubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * ✅ 클럽 관련 비즈니스 로직 처리 서비스
 */
@Service
@RequiredArgsConstructor
public class ClubService {

    private final ClubRepository clubRepository;

    /**
     * ✅ 클럽 생성
     */
    @Transactional
    public ClubEntity createClub(ClubEntity club) {
        return clubRepository.save(club);
    }

    /**
     * ✅ 클럽 수정 (ID로 기존 클럽 찾아 업데이트)
     */
    @Transactional
    public ClubEntity updateClub(Long clubId, ClubEntity changes) {
        ClubEntity existingClub = clubRepository.findById(clubId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 클럽 ID: " + clubId));

        // ✅ 기존 클럽 정보 수정
        existingClub.setTitle(changes.getTitle());
        existingClub.setDescription(changes.getDescription());
        existingClub.setMinParticipants(changes.getMinParticipants());
        existingClub.setMaxParticipants(changes.getMaxParticipants());
        existingClub.setApplicationDeadline(changes.getApplicationDeadline());
        existingClub.setActivityDurationDays(changes.getActivityDurationDays());
        existingClub.setStatus(changes.getStatus());

        return clubRepository.save(existingClub);
    }

    /**
     * ✅ 클럽 삭제
     */
    @Transactional
    public void deleteClub(Long clubId) {
        clubRepository.deleteById(clubId);
    }

    /**
     * ✅ 클럽 조회 (ID로)
     */
    @Transactional(readOnly = true)
    public ClubEntity getClub(Long clubId) {
        return clubRepository.findById(clubId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 클럽 ID: " + clubId));
    }

    /**
     * ✅ 모든 클럽 목록 조회
     */
    @Transactional(readOnly = true)
    public List<ClubEntity> listClubs() {
        return clubRepository.findAll();
    }
}
