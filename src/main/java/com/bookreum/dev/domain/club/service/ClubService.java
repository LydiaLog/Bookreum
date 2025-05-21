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

    @Transactional
    public ClubEntity createClub(ClubEntity club, MultipartFile coverImage, String coverUrlIfProvided) {
        String finalCover = coverImage != null && !coverImage.isEmpty()
            ? fileStorageService.storeFile(coverImage)
            : (coverUrlIfProvided != null && !coverUrlIfProvided.isBlank()
                ? coverUrlIfProvided
                : club.getBook().getCoverImageUrl());

        club.setCoverImageUrl(finalCover);

        club.updateStatus(); // ✅ 상태 계산 후 저장

        return clubRepository.save(club);
    }

    @Transactional
    public ClubEntity updateClub(Integer clubId, ClubEntity changes) {
        ClubEntity existingClub = clubRepository.findById(clubId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 클럽 ID: " + clubId));

        existingClub.setTitle(changes.getTitle());
        existingClub.setDescription(changes.getDescription());
        existingClub.setMinParticipants(changes.getMinParticipants());
        existingClub.setMaxParticipants(changes.getMaxParticipants());
        existingClub.setApplicationDeadline(changes.getApplicationDeadline());
        existingClub.setActivityDurationDays(changes.getActivityDurationDays());

        existingClub.updateStatus(); // ✅ 수정 시 상태도 다시 확인

        return clubRepository.save(existingClub);
    }

    @Transactional
    public void deleteClub(Integer clubId) {
        clubRepository.deleteById(clubId);
    }

    @Transactional(readOnly = true)
    public ClubEntity getClub(Integer clubId) {
        return clubRepository.findById(clubId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 클럽 ID: " + clubId));
    }

    @Transactional(readOnly = true)
    public Page<ClubDTO> listOrSearchClubs(String keyword, String sort, Pageable pageable) {
        updateClosedClubs(); // ✅ 조회 시 마감 자동 처리

        Page<ClubEntity> page;
        if (keyword == null || keyword.isBlank()) {
            page = "oldest".equalsIgnoreCase(sort)
                ? clubRepository.findAllWithBookOldest(pageable)
                : clubRepository.findAllWithBook(pageable);
        } else {
            page = "oldest".equalsIgnoreCase(sort)
                ? clubRepository.searchByKeywordOrderByOldestWithBook(keyword, pageable)
                : clubRepository.searchByKeywordOrderByLatestWithBook(keyword, pageable);
        }

        return page.map(ClubDTO::fromEntity);
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


    @Transactional(readOnly = true)
    public List<ClubEntity> getLatestClubs(Pageable pageable) {
        return clubRepository.findAllByOrderByCreatedAtDesc(pageable).getContent();
    }

    @Transactional
    public void checkAndUpdateClubStatus(ClubEntity club) {
        club.updateStatus(); // 마감기한 또는 인원 수 초과 확인
        clubRepository.save(club); // 상태 변경 시 반영
    }

    
    
}


