package com.bookreum.dev.domain.club.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bookreum.dev.domain.club.entity.ClubApplicationEntity;
import com.bookreum.dev.domain.club.entity.ClubEntity;
import com.bookreum.dev.domain.club.service.ClubApplicationService;
import com.bookreum.dev.domain.club.service.ClubService;
import com.bookreum.dev.domain.user.UserEntity;
import com.bookreum.dev.domain.user.UserRepository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * ✅ 북클럽 신청 관련 REST API 컨트롤러
 * - 클럽 신청, 취소, 상태 확인 등의 기능을 제공합니다.
 */
@RestController
@RequestMapping("/api/clubs/{clubId}/applications")
@RequiredArgsConstructor
public class ClubApplicationController {

    private final ClubApplicationService applicationService;
    private final ClubService clubService;
    private final UserRepository userRepository;

    /**
     * ✅ 신청 요청 DTO
     */
    @Data
    @AllArgsConstructor
    public static class ApplicationRequest {
        private Long userId;
    }

    /**
     * ✅ 신청 취소 요청 DTO
     */
    @Data
    @AllArgsConstructor
    public static class CancelRequest {
        private Long userId;
    }

    /**
     * ✅ 사용자가 클럽에 신청합니다.
     * - 최대 참가 인원 초과 시 예외 발생
     * - 중복 신청 방지 (이미 신청된 클럽 확인)
     * 
     * @param clubId   모임 ID (경로 변수)
     * @param request  신청 요청 (사용자 ID 포함)
     * @return 생성된 ClubApplication 과 HTTP 201 (Created)
     */
    @PostMapping
    public ResponseEntity<ClubApplicationEntity> apply(
            @PathVariable Long clubId,
            @RequestBody ApplicationRequest request
    ) {
        ClubEntity club = validateClubAndUser(clubId, request.getUserId());
        ClubApplicationEntity created = applicationService.applyToClub(club, club.getUser());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * ✅ 사용자의 클럽 신청을 취소합니다.
     * - 이미 신청된 클럽만 취소 가능
     * 
     * @param clubId   모임 ID (경로 변수)
     * @param request  취소 요청 (사용자 ID 포함)
     * @return HTTP 204 (No Content)
     */
    @DeleteMapping
    public ResponseEntity<Void> cancel(
            @PathVariable Long clubId,
            @RequestBody CancelRequest request
    ) {
        ClubEntity club = validateClubAndUser(clubId, request.getUserId());
        applicationService.cancelApplication(club, club.getUser());
        return ResponseEntity.noContent().build();
    }

    /**
     * ✅ 특정 클럽의 모든 신청 목록 조회
     * 
     * @param clubId  모임 ID (경로 변수)
     * @return 신청된 ClubApplication 리스트 (HTTP 200)
     */
    @GetMapping
    public ResponseEntity<List<ClubApplicationEntity>> listApplications(@PathVariable Long clubId) {
        ClubEntity club = clubService.getClub(clubId);
        List<ClubApplicationEntity> apps = applicationService.getApplications(club);
        return ResponseEntity.ok(apps);
    }

    /**
     * ✅ 사용자가 해당 클럽에 신청했는지 여부 확인
     * 
     * @param clubId  모임 ID (경로 변수)
     * @param userId  사용자 ID (쿼리 파라미터)
     * @return 신청 여부 (HTTP 200)
     */
    @GetMapping("/status")
    public ResponseEntity<StatusResponse> checkStatus(
            @PathVariable Long clubId,
            @RequestParam Long userId
    ) {
        ClubEntity club = validateClubAndUser(clubId, userId);
        boolean applied = applicationService.isApplied(club, club.getUser());
        return ResponseEntity.ok(new StatusResponse(applied));
    }

    /**
     * ✅ 클럽과 사용자 검증 메서드 (중복 코드 제거)
     * 
     * @param clubId  모임 ID
     * @param userId  사용자 ID
     * @return 검증된 ClubEntity
     */
    private ClubEntity validateClubAndUser(Long clubId, Long userId) {
        ClubEntity club = clubService.getClub(clubId);
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. ID=" + userId));
        club.setUser(user);
        return club;
    }

    /**
     * ✅ 신청 상태 응답 DTO
     * - 사용자가 해당 클럽에 신청했는지 여부를 반환
     */
    @Data
    @AllArgsConstructor
    public static class StatusResponse {
        private boolean applied;
    }
}
