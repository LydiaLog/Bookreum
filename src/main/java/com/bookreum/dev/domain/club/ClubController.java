package com.bookreum.dev.domain.club;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/clubs")
@RequiredArgsConstructor
public class ClubController {
	 private final ClubService clubService;

	    @Operation(summary = "북클럽 모집글 목록 조회")
	    @GetMapping
	    public List<ClubDTO> getAllClubs() {
	        return clubService.getAllClubs();
	    }

	    @Operation(summary = "북클럽 모집글 작성")
	    @PostMapping
	    public ResponseEntity<ClubDTO> createClub(@RequestBody ClubDTO dto) {
	        return ResponseEntity.status(HttpStatus.CREATED).body(clubService.createClub(dto));
	    }
}
