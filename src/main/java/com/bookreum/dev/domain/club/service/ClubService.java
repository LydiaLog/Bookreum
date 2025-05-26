package com.bookreum.dev.domain.club.service;

import com.bookreum.dev.domain.club.dto.ClubDTO;
import com.bookreum.dev.domain.club.entity.ClubEntity;
import com.bookreum.dev.domain.club.entity.ClubStatus;
import com.bookreum.dev.domain.club.repository.ClubApplicationRepository;
import com.bookreum.dev.domain.club.repository.ClubRepository;
import com.bookreum.dev.global.FileStorageService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClubService {
    private final ClubRepository clubRepository;
    private final ClubApplicationRepository applicationRepository;
    private final FileStorageService fileStorageService;

    /** 모임 생성 */
    @Transactional
    public ClubEntity createClub(ClubEntity club, MultipartFile coverImage, String coverUrlIfProvided) {
        String finalCover = null;
        if (coverImage != null && !coverImage.isEmpty()) {
            finalCover = fileStorageService.storeFile(coverImage);
        } else if (coverUrlIfProvided != null && !coverUrlIfProvided.isBlank()) {
            finalCover = coverUrlIfProvided;
        } else if (club.getBook() != null && club.getBook().getCoverImageUrl() != null) {
            finalCover = club.getBook().getCoverImageUrl();
        } else {
            finalCover = "/uploads/temp/profile.jpg"; // 프로필과 동일한 기본 이미지
        }
        if (finalCover != null) {
            club.setCoverImageUrl(finalCover);
        }
        // 생성 시점에는 항상 OPEN 상태로 설정
        club.setStatus(ClubStatus.OPEN);
        return clubRepository.save(club);
    }
    
    /** 단일 모임 조회 */
    @Transactional(readOnly = true)
    public ClubEntity getClub(Integer clubId) {
    	return clubRepository.findByIdWithBook(clubId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 클럽 ID: " + clubId));
    }

    /** 모임 수정 */
    @Transactional
    public ClubEntity updateClub(Integer clubId, ClubEntity changes) {
        ClubEntity existing = getClub(clubId);
        existing.setTitle(changes.getTitle());
        existing.setDescription(changes.getDescription());
        existing.setMinParticipants(changes.getMinParticipants());
        existing.setMaxParticipants(changes.getMaxParticipants());
        existing.setApplicationDeadline(changes.getApplicationDeadline());
        existing.setActivityDurationDays(changes.getActivityDurationDays());
        existing.setStatus(changes.getStatus());
        return clubRepository.save(existing);
    }

    /** 모임 삭제 */
    @Transactional
    public void deleteClub(Integer clubId) {
        clubRepository.deleteById(clubId);
    }

    /** 모임 목록 조회/검색 (페이징, DTO 변환) */
    @Transactional(readOnly = true)
    public Page<ClubDTO> listOrSearchClubs(String keyword, String sort, String status, Pageable pageable) {
        Page<ClubEntity> page;
        ClubStatus cs = (status != null && !status.isBlank())
            ? ClubStatus.valueOf(status)
            : null;

        if (keyword == null || keyword.isBlank()) {
            page = "oldest".equalsIgnoreCase(sort)
                 ? clubRepository.findAllWithBookOldestAndStatus(cs, pageable)
                 : clubRepository.findAllWithBookAndStatus(cs, pageable);
        } else {
            page = "oldest".equalsIgnoreCase(sort)
                 ? clubRepository.searchByKeywordOrderByOldestWithBookAndStatus(keyword, cs, pageable)
                 : clubRepository.searchByKeywordOrderByLatestWithBookAndStatus(keyword, cs, pageable);
        }

        return page.map(ClubDTO::fromEntity);
    }

    /** 최신 모임 조회 (DTO 리스트) */
    @Transactional(readOnly = true)
    public List<ClubDTO> getLatestClubs(Pageable pageable) {
        return clubRepository
            .findAllByOrderByCreatedAtDesc(pageable)
            .getContent()
            .stream()
            .map(ClubDTO::fromEntity)
            .collect(Collectors.toList());
    }

    /** 마감된 모임 목록 조회 (책 정보 포함) */
    @Transactional(readOnly = true)
    public Page<ClubEntity> findAllClosedClubsWithBook(Pageable pageable) {
        return clubRepository.findAllClosedClubsWithBook(pageable);
    }

    /** 특정 모임 상태 즉시 갱신 후 저장 */
    @Transactional
    public void checkAndUpdateClubStatus(ClubEntity club) {
        long count = applicationRepository.countByClub(club);
        club.updateStatus((int) count);
        clubRepository.save(club);
    }

    /** (선택) 전체 모임 상태 일괄 갱신 */
    @Transactional
    public void updateClosedClubs() {
        clubRepository.findAll().forEach(c -> {
            ClubStatus prev = c.getStatus();
            long count = applicationRepository.countByClub(c);
            c.updateStatus((int) count);
            if (c.getStatus() != prev) {
                clubRepository.save(c);
            }
        });
    }

    /** 참가자 ID 목록 조회 */
    @Transactional(readOnly = true)
    public List<Integer> listParticipantIds(Integer clubId) {
        ClubEntity club = getClub(clubId);
        return applicationRepository.findByClub(club).stream()
            .map(a -> a.getUser().getId())
            .collect(Collectors.toList());
    }
}
