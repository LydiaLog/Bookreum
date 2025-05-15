
package com.bookreum.dev.domain.club.dto;

import java.time.LocalDateTime;

import com.bookreum.dev.domain.club.entity.ClubApplicationEntity;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClubApplicationDTO {
    private Integer id;
    private Integer clubId;
    private Integer userId;
    private LocalDateTime appliedAt;

    /**
     * 엔티티 → DTO 변환 (null 안전성 처리)
     */
    public static ClubApplicationDTO fromEntity(ClubApplicationEntity e) {
        return ClubApplicationDTO.builder()
            .id(e.getId())
            .clubId(e.getClub() != null ? e.getClub().getId() : null)
            .userId(e.getUser() != null ? e.getUser().getId() : null)
            .appliedAt(e.getAppliedAt())
            .build();
    }
}
