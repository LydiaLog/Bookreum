package com.bookreum.dev.domain.club;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClubDTO {
	private Long id;
    private String title;
    private String description;

    public static ClubDTO from(ClubEntity club) {
        return ClubDTO.builder()
                .id(club.getId())
                .title(club.getTitle())
                .description(club.getDescription())
                .build();
    }
}
