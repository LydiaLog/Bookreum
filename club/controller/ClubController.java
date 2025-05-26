package com.bookreum.dev.domain.club.controller;

import java.time.LocalDateTime;
import java.util.List;

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
import com.bookreum.dev.domain.club.entity.ClubStatus;
import com.bookreum.dev.domain.club.service.ClubService;
import com.bookreum.dev.domain.user.UserEntity;
import com.bookreum.dev.domain.user.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

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
        return bookRepository
            .findByTitleAndAuthor(item.getTitle(), item.getAuthor())
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
        @RequestParam("title") String title,
        @RequestParam("description") String description,
        @RequestParam("minParticipants") Integer minParticipants,
        @RequestParam("maxParticipants") Integer maxParticipants,
        @RequestParam("applicationDeadline") String applicationDeadline,
        @RequestParam("activityDurationDays") Integer activityDurationDays,
        @RequestParam("status") String status,
        @RequestParam("bookId") Integer bookId,
        @RequestParam("userId") Integer userId,
        @RequestParam(value = "clubCoverImageUrl", required = false) String coverUrl,
        @RequestPart(value = "coverImage", required = false) MultipartFile coverImage
    ) {
        UserEntity user = userService.getUserEntity(userId);
        BookEntity book = bookRepository.findById(bookId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "책을 찾을 수 없습니다. id=" + bookId
            ));
            
        ClubStatus clubStatus;
        try {
            clubStatus = ClubStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, 
                "유효하지 않은 상태값입니다. 가능한 값: OPEN, MATCHED, CLOSED"
            );
        }

        LocalDateTime deadline;
        try {
            deadline = LocalDateTime.parse(applicationDeadline);
        } catch (Exception e) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "유효하지 않은 날짜 형식입니다."
            );
        }
            
        ClubDTO dto = ClubDTO.builder()
            .title(title)
            .description(description)
            .minParticipants(minParticipants)
            .maxParticipants(maxParticipants)
            .applicationDeadline(deadline)
            .activityDurationDays(activityDurationDays)
            .status(clubStatus)
            .build();
            
        ClubEntity toSave = dto.toEntity(user, book, null);
        ClubEntity saved = clubService.createClub(toSave, coverImage, coverUrl);
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(ClubDTO.fromEntity(saved));
    }

    @GetMapping("/{clubId}")
    public ResponseEntity<ClubDTO> getClub(@PathVariable("clubId") Integer clubId) {
        ClubEntity club = clubService.getClub(clubId);
        return ResponseEntity.ok(ClubDTO.fromEntity(club));
    }

    @GetMapping
    public ResponseEntity<Page<ClubDTO>> listOrSearchClubs(
        @RequestParam(value = "keyword", required = false) String keyword,
        @RequestParam(value = "sort", defaultValue = "latest") String sort,
        @RequestParam(value = "status", required = false) String status,
        Pageable pageable
    ) {
        Page<ClubDTO> page = clubService.listOrSearchClubs(
            keyword,
            sort,
            status,
            pageable
        );
        return ResponseEntity.ok(page);
    }

    @DeleteMapping("/{clubId}")
    public ResponseEntity<Void> deleteClub(@PathVariable("clubId") Integer clubId) {
        clubService.deleteClub(clubId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/public")
    public ResponseEntity<Page<ClubDTO>> getPublicClubs(
        @RequestParam(value = "status", required = false) String status,
        Pageable pageable
    ) {
        Page<ClubDTO> page = clubService.listOrSearchClubs(
            null,
            "latest",
            status,
            pageable
        );
        return ResponseEntity.ok(page);
    }

    @GetMapping("/public/latest")
    public ResponseEntity<List<ClubDTO>> getLatestClubs(
        @RequestParam(value = "size", defaultValue = "2") int size
    ) {
        var list = clubService.getLatestClubs(Pageable.ofSize(size));
        return ResponseEntity.ok(list);
    }

    @GetMapping("/closed")
    public ResponseEntity<Page<ClubDTO>> getClosedClubs(Pageable pageable) {
        Page<ClubDTO> page = clubService
            .findAllClosedClubsWithBook(pageable)
            .map(ClubDTO::fromEntity);
        return ResponseEntity.ok(page);
    }

    @PutMapping("/{clubId}/status")
    public ResponseEntity<ClubDTO> updateClubStatus(@PathVariable("clubId") Integer clubId) {
        ClubEntity club = clubService.getClub(clubId);
        clubService.checkAndUpdateClubStatus(club);
        return ResponseEntity.ok(ClubDTO.fromEntity(club));
    }
    @PutMapping("/{clubId}")
    public ResponseEntity<ClubDTO> updateClub(
        @PathVariable("clubId") Integer clubId,
        @RequestParam("title") String title,
        @RequestParam("description") String description,
        @RequestParam("minParticipants") Integer minParticipants,
        @RequestParam("maxParticipants") Integer maxParticipants,
        @RequestParam("applicationDeadline") String applicationDeadline,
        @RequestParam("activityDurationDays") Integer activityDurationDays,
        @RequestParam("status") String status,
        @RequestParam("bookId") Integer bookId,
        @RequestParam("userId") Integer userId
    ) {
        UserEntity user = userService.getUserEntity(userId);
        BookEntity book = bookRepository.findById(bookId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "책을 찾을 수 없습니다. id=" + bookId
            ));
            
        ClubStatus clubStatus;
        try {
            clubStatus = ClubStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, 
                "유효하지 않은 상태값입니다. 가능한 값: OPEN, MATCHED, CLOSED"
            );
        }

        LocalDateTime deadline;
        try {
            deadline = LocalDateTime.parse(applicationDeadline);
        } catch (Exception e) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "유효하지 않은 날짜 형식입니다."
            );
        }
            
        ClubDTO dto = ClubDTO.builder()
            .title(title)
            .description(description)
            .minParticipants(minParticipants)
            .maxParticipants(maxParticipants)
            .applicationDeadline(deadline)
            .activityDurationDays(activityDurationDays)
            .status(clubStatus)
            .build();
            
        ClubEntity changes = dto.toEntity(user, book, null);
        ClubEntity updated = clubService.updateClub(clubId, changes);
        return ResponseEntity.ok(ClubDTO.fromEntity(updated));
    }
}
