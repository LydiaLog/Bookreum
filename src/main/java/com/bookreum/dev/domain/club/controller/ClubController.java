package com.bookreum.dev.domain.club.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.bookreum.dev.domain.book.AladinBookService;
import com.bookreum.dev.domain.book.AladinItem;
import com.bookreum.dev.domain.book.BookEntity;
import com.bookreum.dev.domain.book.BookRepository;
import com.bookreum.dev.domain.club.dto.ClubDTO;
import com.bookreum.dev.domain.club.entity.ClubEntity;
import com.bookreum.dev.domain.club.service.ClubService;
import com.bookreum.dev.domain.user.UserEntity;
import com.bookreum.dev.domain.user.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
/**
 * Club(모임) CRUD를 위한 REST 컨트롤러
 */
@RestController
@RequestMapping("/api/clubs")
@RequiredArgsConstructor
public class ClubController {

    private final ClubService clubService;
    private final UserService userService;
    private final AladinBookService aladinBookService; 
    private final BookRepository bookRepository;        

    /** 
     * 클럽 생성 전, 키워드로 책 검색 (알라딘 API 중계) 
     */
    @GetMapping("/searchBooks")
    public List<AladinItem> searchBooks(@RequestParam String keyword) {
        var result = aladinBookService.searchBooks(keyword);
        if (result.getTotalResults() == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "검색 결과가 없습니다.");
        }
        return result.getItem();
    }
    /**
     * 사용자가 선택한 책 저장 (중복 방지)
     */
    @PostMapping("/saveBook")
    public BookEntity saveBook(@RequestBody AladinItem item) {
        return bookRepository.findByTitleAndAuthor(item.getTitle(), item.getAuthor())
            .orElseGet(() -> bookRepository.save(
                BookEntity.builder()
                    .title(item.getTitle())
                    .author(item.getAuthor())
                    .coverImageUrl(item.getCover())
                    .build()
            ));
    }

    /**
     * 모임 생성 (요청에 bookId 포함)
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ClubDTO> createClub(
        @RequestPart("club") @Valid ClubDTO dto,
        @RequestParam("userId") Integer userId,
        @RequestParam(value = "coverUrl", required = false) String coverUrl,
        @RequestPart(value = "coverImage", required = false) MultipartFile coverImage
    ) {
        // 1) 사용자, 책 조회
        UserEntity user = userService.getUserEntity(userId);
        BookEntity book = bookRepository.findById(dto.getBookId())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "책을 찾을 수 없습니다. id=" + dto.getBookId()
            ));
        // 2) DTO → Entity (coverImageUrl 은 빈값으로 일단 생성)
        ClubEntity club = dto.toEntity(user, book, null);
        // 3) 이미지 로직 포함한 저장
        ClubEntity saved = clubService.createClub(club, coverImage, coverUrl);
        // 4) DTO 반환
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(ClubDTO.fromEntity(saved));
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
     * 모든 모임 목록 조회 or 키워드 검색 (페이징, sort=latest|oldest)
     */
    @GetMapping
    public ResponseEntity<Page<ClubDTO>> listOrSearchClubs(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "sort", defaultValue = "latest") String sort,
            Pageable pageable
    ) {
        Page<ClubDTO> page = clubService.listOrSearchClubs(keyword, sort, pageable);
        return ResponseEntity.ok(page);
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




