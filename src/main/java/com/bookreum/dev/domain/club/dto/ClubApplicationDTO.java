
package com.bookreum.dev.domain.club.dto;

import java.time.LocalDateTime;

import com.bookreum.dev.domain.club.entity.ClubApplicationEntity;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClubApplicationDTO {
    private Long id;
    private Long clubId;
    private Long userId;
    private LocalDateTime appliedAt;

    public static ClubApplicationDTO fromEntity(ClubApplicationEntity e) {
        return ClubApplicationDTO.builder()
            .id(e.getId())
            .clubId(e.getClub().getId())
            .userId(e.getUser().getId())
            .appliedAt(e.getAppliedAt())
            .build();
    }
}
