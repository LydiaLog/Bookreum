package com.bookreum.dev.domain.club.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bookreum.dev.domain.club.dto.ClubDTO;
import com.bookreum.dev.domain.club.entity.ClubEntity;
import com.bookreum.dev.domain.club.service.ClubService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


/**
 * Club(모임) CRUD를 위한 REST 컨트롤러
 */
@RestController
@RequestMapping("/api/clubs")
@RequiredArgsConstructor
public class ClubController {

    private final ClubService clubService;

    /**
     * 새로운 모임 생성
     * @param dto 생성할 모임 정보 DTO
     * @return 생성된 모임 DTO와 HTTP 201 상태
     */
    @PostMapping
    public ResponseEntity<ClubDTO> createClub(@RequestBody @Valid ClubDTO dto) {
        // DTO → Entity 변환
        ClubEntity entity = ClubEntity.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .minParticipants(dto.getMinParticipants())
                .maxParticipants(dto.getMaxParticipants())
                .applicationDeadline(dto.getApplicationDeadline())
                .activityDurationDays(dto.getActivityDurationDays())
                .status(dto.getStatus())
                .build();
        ClubEntity created = clubService.createClub(entity);
        // Entity → DTO 변환 및 반환
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ClubDTO.fromEntity(created));
    }

    /**
     * 특정 모임 조회
     * @param clubId 모임 ID
     * @return 조회된 모임 DTO
     */
    @GetMapping("/{clubId}")
    public ResponseEntity<ClubDTO> getClub(@PathVariable Integer clubId) {
        ClubEntity club = clubService.getClub(clubId);
        return ResponseEntity.ok(ClubDTO.fromEntity(club));
    }

    /**
     * 모든 모임 목록 조회
     * @return 모임 DTO 리스트
     */
    @GetMapping
    public ResponseEntity<List<ClubDTO>> listClubs() {
        List<ClubDTO> dtos = clubService.listClubs().stream()
                .map(ClubDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    /**
     * 모임 삭제
     * @param clubId 삭제할 모임 ID
     * @return HTTP 204 상태
     */
    @DeleteMapping("/{clubId}")
    public ResponseEntity<Void> deleteClub(@PathVariable Integer clubId) {
        clubService.deleteClub(clubId);
        return ResponseEntity.noContent().build();
    }
}




