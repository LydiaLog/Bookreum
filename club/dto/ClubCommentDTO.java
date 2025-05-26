package com.bookreum.dev.domain.club.dto;

import java.time.LocalDateTime;
import com.bookreum.dev.domain.club.entity.ClubCommentEntity;
import lombok.*;

/**
 * 댓글 정보를 외부에 노출하기 위한 DTO
 */
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ClubCommentDTO {
    private Integer id;
    private Integer clubId;
    private Integer authorId;
    private String authorNickname;
    private String authorProfileImage;
    private String content;
    private LocalDateTime createdAt;
    private Integer likeCount;

    /** 엔티티 -> DTO 변환 메서드 */
    public static ClubCommentDTO fromEntity(ClubCommentEntity e) {
        return ClubCommentDTO.builder()
            .id(e.getId())
            .clubId(e.getClub().getId())
            .authorId(e.getAuthor().getId())
            .authorNickname(e.getAuthor().getNickname())
            .authorProfileImage(e.getAuthor().getProfileImage())
            .content(e.getContent())
            .createdAt(e.getCreatedAt())
            .build();
    }
}
