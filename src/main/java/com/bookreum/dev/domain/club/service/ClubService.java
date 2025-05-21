package com.bookreum.dev.domain.club.service;

import com.bookreum.dev.domain.club.dto.ClubDTO;
import com.bookreum.dev.domain.club.entity.ClubEntity;
import com.bookreum.dev.domain.club.entity.ClubStatus;
import com.bookreum.dev.domain.club.repository.ClubRepository;
import com.bookreum.dev.global.FileStorageService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public ClubEntity createClub(ClubEntity club, MultipartFile coverImage, String coverUrlIfProvided) {
        // 1) 이미지 결정 로직
        String finalCover = null;
        
        if (coverImage != null && !coverImage.isEmpty()) {
            finalCover = fileStorageService.storeFile(coverImage);
        } else if (coverUrlIfProvided != null && !coverUrlIfProvided.isBlank()) {
            finalCover = coverUrlIfProvided;
        } else if (club.getBook() != null && club.getBook().getCoverImageUrl() != null) {
            finalCover = club.getBook().getCoverImageUrl();
        }

        if (finalCover != null) {
            club.setCoverImageUrl(finalCover);
        }
     // 2) 상태 체크 및 설정
        club.updateStatus();
        
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
        		.map(club -> {
                    // BookEntity를 강제로 로드
                    club.getBook().getTitle();
                    return club;
                })
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 클럽 ID: " + clubId));
    }

    /**
     * 클럽 목록 조회 or 검색
     */
    @Transactional(readOnly = true)
    public Page<ClubDTO> listOrSearchClubs(String keyword, String sort, String status, Pageable pageable) {
        Page<ClubEntity> page;
        ClubStatus clubStatus = status != null && !status.isBlank() ? ClubStatus.valueOf(status) : null;

        if (keyword == null || keyword.isBlank()) {
            if ("oldest".equalsIgnoreCase(sort)) {
                page = clubRepository.findAllWithBookOldestAndStatus(clubStatus, pageable);
            } else {
                page = clubRepository.findAllWithBookAndStatus(clubStatus, pageable);
            }
        } else {
            if ("oldest".equalsIgnoreCase(sort)) {
                page = clubRepository.searchByKeywordOrderByOldestWithBookAndStatus(keyword, clubStatus, pageable);
            } else {
                page = clubRepository.searchByKeywordOrderByLatestWithBookAndStatus(keyword, clubStatus, pageable);
            }
        }

        return page.map(ClubDTO::fromEntity);
    }
    
    @Transactional(readOnly = true)
    public List<ClubDTO> getLatestClubs(Pageable pageable) {
        return clubRepository.findAllByOrderByCreatedAtDesc(pageable)
            .getContent()
            .stream()
            .map(ClubDTO::fromEntity)
            .toList();
    }
    @Transactional
    public void updateClosedClubs() {
        List<ClubEntity> clubs = clubRepository.findAll();

        for (ClubEntity club : clubs) {
            ClubStatus previous = club.getStatus();
            club.updateStatus(); // ✅ 상태 갱신 계산
            if (club.getStatus() != previous) {
                // 상태가 바뀐 경우에만 저장 대상으로 유지
                clubRepository.save(club);
            }
        }
    }

    @Transactional
    public void checkAndUpdateClubStatus(ClubEntity club) {
        club.updateStatus(); // 마감기한 또는 인원 수 초과 확인
        clubRepository.save(club); // 상태 변경 시 반영
    }
    /**
     * 마감된 북클럽 목록 조회 (책 정보 포함)
     */
    @Transactional(readOnly = true)
    public Page<ClubEntity> findAllClosedClubsWithBook(Pageable pageable) {
        return clubRepository.findAllClosedClubsWithBook(pageable);
    }
}
