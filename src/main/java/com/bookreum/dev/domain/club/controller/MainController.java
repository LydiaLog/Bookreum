package com.bookreum.dev.domain.club.controller;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookreum.dev.domain.club.dto.ClubDTO;
import com.bookreum.dev.domain.club.service.ClubService;

import lombok.RequiredArgsConstructor;

/**
 * 홈 화면용 컨트롤러: 최신 북클럽 2개를 반환합니다.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MainController {

    private final ClubService clubService;

    /**
     * 홈 화면에 표시할 최신 북클럽 목록 조회
     * @return 최신 생성된 북클럽 2개
     */
    @GetMapping("/main")
    public ResponseEntity<List<ClubDTO>> getLatestBookclubs() {
        List<ClubDTO> latest = clubService.getLatestClubs(PageRequest.of(0, 2));
        return ResponseEntity.ok(latest);
    }
}
