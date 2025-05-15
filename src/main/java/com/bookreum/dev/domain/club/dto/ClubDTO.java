package com.bookreum.dev.domain.club.dto;

import java.time.LocalDateTime;

import com.bookreum.dev.domain.club.entity.ClubEntity;
import com.bookreum.dev.domain.club.entity.ClubStatus;

import lombok.*;

@Getter @Builder
@AllArgsConstructor
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
    private Integer createdById;
    private LocalDateTime createdAt;

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
            .createdById(e.getUser() != null ? e.getUser().getId() : null)
            .createdAt(e.getCreatedAt())
            .build();
    }
}
