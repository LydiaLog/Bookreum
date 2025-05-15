// com/bookreum/dev/domain/club/dto/ClubDTO.java
package com.bookreum.dev.domain.club.dto;

import java.time.LocalDateTime;

import com.bookreum.dev.domain.club.entity.ClubEntity;
import com.bookreum.dev.domain.club.entity.ClubStatus;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClubDTO {
    private Long id;
    private String title;
    private String description;
    private int minParticipants;
    private int maxParticipants;
    private LocalDateTime applicationDeadline;
    private int activityDurationDays;
    private ClubStatus status;
    private Long bookId;
    private Long createdByUserId;
    private LocalDateTime createdAt;

    /** Entity → DTO 변환 편의 메서드 */
    public static ClubDTO fromEntity(ClubEntity e) {
        return ClubDTO.builder()
            .id(e.getId())
            .title(e.getTitle())
            .description(e.getDescription())
            .minParticipants(e.getMinParticipants())
            .maxParticipants(e.getMaxParticipants())
            .applicationDeadline(e.getApplicationDeadline())
            .activityDurationDays(e.getActivityDurationDays())
            .status(e.getStatus())
            .bookId(e.getBook() != null ? e.getBook().getId() : null)
            .createdByUserId(e.getUser() != null ? e.getUser().getId() : null)
            .createdAt(e.getCreatedAt())
            .build();
    }
}
