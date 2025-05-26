package com.bookreum.dev.domain.club.controller;

import java.util.Comparator;
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

    @Data
    static class ApplicationRequest {
        @NotNull(message = "User ID는 필수입니다.")
        private Integer userId;
    }

    @Data
    static class CancelRequest {
        @NotNull(message = "User ID는 필수입니다.")
        private Integer userId;
    }

    /**
     * 모임에 사용자 신청
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
     * 모임 신청 취소
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
     * 특정 모임의 신청 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<ClubApplicationDTO>> listApplications(
    		@PathVariable("clubId") Integer clubId
    ) {
        // 1) ClubEntity 조회 (존재하지 않으면 400/404 예외 발생)
        ClubEntity club = clubService.getClub(clubId);

        // 2) 신청 엔티티 → DTO 변환 후, appliedAt 기준 정렬
        List<ClubApplicationDTO> dtos = applicationService.getApplications(club).stream()
            .map(ClubApplicationDTO::fromEntity)
            .sorted(Comparator.comparing(ClubApplicationDTO::getAppliedAt))
            .toList(); // Java 16+ 사용할 경우

        // 만약 toList()가 안 되면 아래처럼 변경하세요:
        //    .collect(Collectors.toList());

        // 3) 결과 반환
        return ResponseEntity.ok(dtos);
    }

    /**
     * 사용자 신청 상태 확인
     */
    @GetMapping("/status")
    public ResponseEntity<Boolean> checkStatus(
    		 @PathVariable("clubId") Integer clubId,
    		 @RequestParam("userId") @NotNull Integer userId
    ) {
        ClubEntity club = clubService.getClub(clubId);
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자 ID: " + userId));

        boolean applied = applicationService.isApplied(club, user);
        return ResponseEntity.ok(applied);
    }
}
