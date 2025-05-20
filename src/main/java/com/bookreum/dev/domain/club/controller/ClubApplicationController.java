package com.bookreum.dev.domain.club.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bookreum.dev.domain.club.dto.ClubApplicationDTO;
import com.bookreum.dev.domain.club.entity.ClubApplicationEntity;
import com.bookreum.dev.domain.club.entity.ClubEntity;
import com.bookreum.dev.domain.club.service.ClubApplicationService;
import com.bookreum.dev.domain.club.service.ClubService;
import com.bookreum.dev.domain.user.UserEntity;
import com.bookreum.dev.domain.user.UserRepository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * 북클럽 신청 관련 REST API 컨트롤러
 */
@RestController
@RequestMapping("/api/clubs/{clubId}/applications")
@RequiredArgsConstructor
public class ClubApplicationController {

    private final ClubApplicationService applicationService;
    private final ClubService clubService;
    private final UserRepository userRepository;

    /**
     * 신청 요청 DTO
     */
    @Data
    static class ApplicationRequest {
        @NotNull(message = "User ID는 필수입니다.")
        private Integer userId;
    }

    /**
     * 신청 취소 요청 DTO
     */
    @Data
    static class CancelRequest {
        @NotNull(message = "User ID는 필수입니다.")
        private Integer userId;
    }

    /**
     * 사용자 클럽 신청
     * @param clubId 모임 ID
     * @param request 신청 요청 DTO
     * @return 생성된 신청 DTO와 HTTP 201
     */
    @PostMapping
    public ResponseEntity<ClubApplicationDTO> apply(
            @PathVariable("clubId") Integer clubId,
            @RequestBody @Valid ApplicationRequest request
    ) {
        ClubEntity club = clubService.getClub(clubId);
        UserEntity user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자 ID: " + request.getUserId()));
        ClubApplicationEntity app = applicationService.applyToClub(club, user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ClubApplicationDTO.fromEntity(app));
    }

    /**
     * 사용자 클럽 신청 취소
     * @param clubId 모임 ID
     * @param request 취소 요청 DTO
     * @return HTTP 204
     */
    @DeleteMapping
    public ResponseEntity<Void> cancel(
            @PathVariable("clubId") Integer clubId,
            @RequestBody @Valid CancelRequest request
    ) {
        ClubEntity club = clubService.getClub(clubId);
        UserEntity user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자 ID: " + request.getUserId()));
        applicationService.cancelApplication(club, user);
        return ResponseEntity.noContent().build();
    }

    /**
     * 특정 클럽의 신청 목록 조회
     * @param clubId 모임 ID
     * @return 신청 DTO 리스트
     */
    @GetMapping
    public ResponseEntity<List<ClubApplicationDTO>> listApplications(
            @PathVariable("clubId") Integer clubId
    ) {
        ClubEntity club = clubService.getClub(clubId);
        List<ClubApplicationDTO> dtos = applicationService.getApplications(club).stream()
                .map(ClubApplicationDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    /**
     * 사용자 신청 상태 확인
     * @param clubId 모임 ID
     * @param userId 사용자 ID
     * @return 신청 여부 (true/false)
     */
    @GetMapping("/status")
    public ResponseEntity<Boolean> checkStatus(
            @PathVariable("clubId") Integer clubId,
            @RequestParam @NotNull Integer userId
    ) {
        ClubEntity club = clubService.getClub(clubId);
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자 ID: " + userId));
        boolean applied = applicationService.isApplied(club, user);
        return ResponseEntity.ok(applied);
    }
}