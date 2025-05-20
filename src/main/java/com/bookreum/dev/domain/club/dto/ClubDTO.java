package com.bookreum.dev.domain.club.dto;

import java.time.LocalDateTime;

import com.bookreum.dev.domain.book.BookEntity;
import com.bookreum.dev.domain.club.entity.ClubEntity;
import com.bookreum.dev.domain.club.entity.ClubStatus;
import com.bookreum.dev.domain.user.UserEntity;

import lombok.*;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClubDTO {
    private Integer id;
    private String title;
    private String description;
    
    /** 호스트 포함 최소 참가 인원 */
    private Integer minParticipants;
    /** 호스트 포함 최대 참가 인원 */
    private Integer maxParticipants;
    /** 호스트 제외 추가로 필요한 최소 인원 */
    private Integer othersMinRequired;
    /** 호스트 제외 최대 허용 인원 */
    private Integer othersMaxAllowed;
    
    private LocalDateTime applicationDeadline;
    private Integer activityDurationDays;
    private ClubStatus status;
    private Integer bookId;
    private String bookTitle;
    private String bookAuthor;
    private Integer createdById;
    private LocalDateTime createdAt;
    
    private String createdByNickname;
    private String createdByProfileImageUrl;
    private String coverImageUrl;

    public static ClubDTO fromEntity(ClubEntity e) {
        if (e == null) {
            return null;
        }
        int minP = e.getMinParticipants();
        int maxP = e.getMaxParticipants() != null ? e.getMaxParticipants() : 0;
        int othersMin = Math.max(0, minP - 1);
        Integer othersMax = e.getMaxParticipants() != null 
            ? Math.max(0, maxP - 1)
            : null;

        return ClubDTO.builder()
            .id(e.getId())
            .title(e.getTitle())
            .description(e.getDescription())
            .minParticipants(minP)
            .maxParticipants(maxP)
            .othersMinRequired(othersMin)
            .othersMaxAllowed(othersMax)
            .applicationDeadline(e.getApplicationDeadline())
            .activityDurationDays(e.getActivityDurationDays())
            .status(e.getStatus())
            .bookId(e.getBook() != null ? e.getBook().getId() : null)
            .bookTitle(e.getBook() != null ? e.getBook().getTitle() : null)
            .bookAuthor(e.getBook() != null ? e.getBook().getAuthor() : null)
            .createdById(e.getUser() != null ? e.getUser().getId() : null)
            .createdAt(e.getCreatedAt())
            .createdByNickname(e.getUser() != null ? e.getUser().getNickname() : null)
            .createdByProfileImageUrl(e.getUser() != null ? e.getUser().getProfileImage() : null)
            .coverImageUrl(e.getCoverImageUrl())
            .build();
    }
    /** ClubEntity 빌드를 위해 bookId를 Entity로 매핑할 때 사용 */
    public ClubEntity toEntity(UserEntity creator, BookEntity book,String coverUrl) {
        return ClubEntity.builder()
                .title(title)
                .description(description)
                .minParticipants(minParticipants)
                .maxParticipants(maxParticipants)
                .applicationDeadline(applicationDeadline)
                .activityDurationDays(activityDurationDays)
                .status(status)
                .user(creator)
                .book(book)        
                .coverImageUrl(coverUrl)
                .build();
    }
}
