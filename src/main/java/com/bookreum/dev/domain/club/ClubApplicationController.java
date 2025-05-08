package com.bookreum.dev.domain.club;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bookreum.dev.domain.user.UserEntity;
import com.bookreum.dev.domain.user.UserRepository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * 북클럽 신청 관련 REST API
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
    @AllArgsConstructor
    public static class ApplicationRequest {
        private Long userId;
    }

    /**
     * 신청 취소 DTO
     */
    @Data
    @AllArgsConstructor
    public static class CancelRequest {
        private Long userId;
    }

    /**
     * 사용자가 클럽에 신청합니다.
     * @param clubId   모임 ID
     * @param request  { "userId": 123 }
     * @return 생성된 ClubApplication 과 HTTP 201
     */
    @PostMapping
    public ResponseEntity<ClubApplication> apply(
            @PathVariable Long clubId,
            @RequestBody ApplicationRequest request
    ) {
        ClubEntity club = clubService.getClub(clubId);
        UserEntity user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. ID=" + request.getUserId()));

        ClubApplication created = applicationService.applyToClub(club, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * 사용자의 클럽 신청을 취소합니다.
     * @param clubId   모임 ID
     * @param request  { "userId": 123 }
     * @return HTTP 204
     */
    @DeleteMapping
    public ResponseEntity<Void> cancel(
            @PathVariable Long clubId,
            @RequestBody CancelRequest request
    ) {
        ClubEntity club = clubService.getClub(clubId);
        UserEntity user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. ID=" + request.getUserId()));

        applicationService.cancelApplication(club, user);
        return ResponseEntity.noContent().build();
    }

    /**
     * 특정 클럽의 모든 신청 목록 조회
     * @param clubId  모임 ID
     * @return ClubApplication 리스트
     */
    @GetMapping
    public ResponseEntity<List<ClubApplication>> listApplications(@PathVariable Long clubId) {
        ClubEntity club = clubService.getClub(clubId);
        List<ClubApplication> apps = applicationService.getApplications(club);
        return ResponseEntity.ok(apps);
    }

    /**
     * 사용자가 해당 클럽에 신청했는지 여부 확인
     * @param clubId  모임 ID
     * @param userId  사용자 ID (쿼리 파라미터)
     * @return { "applied": true/false }
     */
    @GetMapping("/status")
    public ResponseEntity<StatusResponse> checkStatus(
            @PathVariable Long clubId,
            @RequestParam Long userId
    ) {
        ClubEntity club = clubService.getClub(clubId);
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. ID=" + userId));

        boolean applied = applicationService.isApplied(club, user);
        return ResponseEntity.ok(new StatusResponse(applied));
    }

    /**
     * 신청 상태 응답 DTO
     */
    @Data
    @AllArgsConstructor
    public static class StatusResponse {
        private boolean applied;
    }
}
