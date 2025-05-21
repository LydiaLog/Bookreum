package com.bookreum.dev.domain.club.controller;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
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

    @GetMapping("/searchBooks")
    public List<AladinItem> searchBooks(@RequestParam String keyword) {
        var result = aladinBookService.searchBooks(keyword);
        if (result.getTotalResults() == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "검색 결과가 없습니다.");
        }
        return result.getItem();
    }

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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ClubDTO> createClub(
        @RequestPart("club") @Valid ClubDTO dto,
        @RequestParam("userId") Integer userId,
        @RequestParam(value = "coverUrl", required = false) String coverUrl,
        @RequestPart(value = "coverImage", required = false) MultipartFile coverImage
    ) {
        try {
            UserEntity user = userService.getUserEntity(userId);
            BookEntity book = bookRepository.findById(dto.getBookId())
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "책을 찾을 수 없습니다. id=" + dto.getBookId()
                ));

            ClubEntity club = dto.toEntity(user, book, coverUrl);
            ClubEntity saved = clubService.createClub(club, coverImage, coverUrl);

            return ResponseEntity.status(HttpStatus.CREATED)
                                 .body(ClubDTO.fromEntity(saved));
        } catch (Exception e) {
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "북클럽 생성 중 오류가 발생했습니다: " + e.getMessage()
            );
        }
    }

    @GetMapping("/{clubId}")
    public ResponseEntity<ClubDTO> getClub(@PathVariable Integer clubId) {
        ClubEntity club = clubService.getClub(clubId);
        clubService.checkAndUpdateClubStatus(club); // ✅ 상태 점검
        return ResponseEntity.ok(ClubDTO.fromEntity(club));
    }

    @GetMapping
    public ResponseEntity<Page<ClubDTO>> listOrSearchClubs(
        @RequestParam(value = "keyword", required = false) String keyword,
        @RequestParam(value = "sort", defaultValue = "latest") String sort,
        Pageable pageable
    ) {
        Page<ClubDTO> page = clubService.listOrSearchClubs(keyword, sort, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/public/latest")
    public ResponseEntity<List<ClubDTO>> getLatestPublicClubs() {
        Pageable topTwo = PageRequest.of(0, 2);
        List<ClubEntity> clubs = clubService.getLatestClubs(topTwo);

        // ✅ 각 클럽 상태 점검 (마감 여부 반영)
        clubs.forEach(clubService::checkAndUpdateClubStatus);

        // ✅ DTO 변환 후 응답
        return ResponseEntity.ok(
            clubs.stream().map(ClubDTO::fromEntity).toList()
        );
    }


    @DeleteMapping("/{clubId}")
    public ResponseEntity<Void> deleteClub(@PathVariable Integer clubId) {
        clubService.deleteClub(clubId);
        return ResponseEntity.noContent().build();
    }
}
