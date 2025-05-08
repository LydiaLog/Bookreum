package com.bookreum.dev.domain.club;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.bookreum.dev.domain.club.ClubEntity;
import com.bookreum.dev.domain.club.ClubService;  // 실제 서비스 구현체

import lombok.RequiredArgsConstructor;

import com.bookreum.dev.domain.club.ClubDTO;


/**
 * Club(모임) CRUD를 위한 REST 컨트롤러
 */
@RestController
@RequestMapping("/api/clubs")
@RequiredArgsConstructor
public class ClubController {

    private final ClubService clubService;

    /**
     * 새 모임 생성
     * @param club 생성할 모임 데이터
     * @return 생성된 모임 정보와 HTTP 201 상태
     */
    @PostMapping
    public ResponseEntity<ClubEntity> createClub(@RequestBody ClubEntity club) {
        ClubEntity created = clubService.createClub(club);
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(created);
    }

    /**
     * 특정 모임 조회
     * @param id 모임 ID
     * @return 조회된 모임 정보
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClubEntity> getClub(@PathVariable Long id) {
        ClubEntity club = clubService.getClub(id);
        return ResponseEntity.ok(club);
    }

    /**
     * 모든 모임 목록 조회
     * @return 모임 목록
     */
    @GetMapping
    public ResponseEntity<List<ClubEntity>> listClubs() {
        List<ClubEntity> clubs = clubService.listClubs();
        return ResponseEntity.ok(clubs);
    }

    /**
     * 모임 삭제
     * @param id 삭제할 모임 ID
     * @return HTTP 204 상태
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClub(@PathVariable Long id) {
        clubService.deleteClub(id);
        return ResponseEntity.noContent().build();
    }
}



