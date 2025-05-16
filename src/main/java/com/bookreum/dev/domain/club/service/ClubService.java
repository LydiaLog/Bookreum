package com.bookreum.dev.domain.club.service;

import com.bookreum.dev.domain.club.config.FileStorageService;
import com.bookreum.dev.domain.club.entity.ClubEntity;
import com.bookreum.dev.domain.club.repository.ClubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 북클럽(모임) 기본 CRUD 및 조회 처리를 담당하는 서비스 레이어
 */
@Service
@RequiredArgsConstructor
public class ClubService {

    private final ClubRepository clubRepository;
    private final FileStorageService fileStorageService;
    /**
     * 새로운 클럽(모임)을 생성하고 저장합니다.
     * @param club 생성할 모임 엔티티 정보
     * @return 저장된 ClubEntity
     */
    @Transactional
    public ClubEntity createClub(ClubEntity club, MultipartFile coverImage,String coverUrlIfProvided ) {
    	 // 1) 이미지 결정 로직
        String finalCover = coverImage != null && !coverImage.isEmpty()
            ? fileStorageService.storeFile(coverImage)
            : (coverUrlIfProvided != null && !coverUrlIfProvided.isBlank()
                ? coverUrlIfProvided
                : club.getBook().getCoverImageUrl());

        club.setCoverImageUrl(finalCover);

    	return clubRepository.save(club);
    }

    /**
     * 기존 클럽 정보를 업데이트합니다.
     * @param clubId 수정할 모임 ID
     * @param changes 수정할 필드가 담긴 ClubEntity (제목, 설명 등)
     * @return 업데이트된 ClubEntity
     * @throws IllegalArgumentException 존재하지 않는 ID인 경우
     */
    @Transactional
    public ClubEntity updateClub(Integer clubId, ClubEntity changes) {
        // 1) 기존 클럽 조회
        ClubEntity existingClub = clubRepository.findById(clubId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 클럽 ID: " + clubId));

        // 2) 필드별 값 덮어쓰기
        existingClub.setTitle(changes.getTitle());
        existingClub.setDescription(changes.getDescription());
        existingClub.setMinParticipants(changes.getMinParticipants());
        existingClub.setMaxParticipants(changes.getMaxParticipants());
        existingClub.setApplicationDeadline(changes.getApplicationDeadline());
        existingClub.setActivityDurationDays(changes.getActivityDurationDays());
        existingClub.setStatus(changes.getStatus());

        // 3) 변경 후 저장
        return clubRepository.save(existingClub);
    }

    /**
     * 클럽을 삭제합니다.
     * @param clubId 삭제할 모임 ID
     */
    @Transactional
    public void deleteClub(Integer clubId) {
        clubRepository.deleteById(clubId);
    }

    /**
     * 단일 클럽 조회 (읽기 전용)
     * @param clubId 조회할 모임 ID
     * @return ClubEntity
     * @throws IllegalArgumentException 존재하지 않는 ID인 경우
     */
    @Transactional(readOnly = true)
    public ClubEntity getClub(Integer clubId) {
        return clubRepository.findById(clubId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 클럽 ID: " + clubId));
    }

    /**
     * 모든 클럽 목록을 조회합니다.
     * @return ClubEntity 리스트
     */
    @Transactional(readOnly = true)
    public List<ClubEntity> listClubs() {
        return clubRepository.findAll();
    }
}
