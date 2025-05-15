package com.bookreum.dev.domain.post.dto;

import java.time.format.DateTimeFormatter;

import com.bookreum.dev.domain.post.entity.PostHeart;

import lombok.*;

/**
 * 게시글 좋아요 토글 요청 및 응답 DTO
 */
public class PostHeartDto {

    /**
     * 좋아요 토글 요청용 DTO
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        private Integer userId;   // 사용자 PK
        private Integer postId;   // 게시글 PK
    }

    /**
     * 좋아요 응답용 DTO
     */
    @Getter
    @Builder
    @AllArgsConstructor
    public static class Response {
        private Integer id;       // 좋아요 PK
        private Integer userId;   // 사용자 PK
        private Integer postId;   // 게시글 PK
        private String createdAt; // 생성일시 (ISO)

        /**
         * 엔티티 기반 DTO 변환
         */
        public static Response fromEntity(PostHeart entity) {
            return Response.builder()
                    .id(entity.getId())
                    .userId(entity.getUser().getId())
                    .postId(entity.getPost().getId())
                    .createdAt(entity.getCreatedAt()
                            .format(DateTimeFormatter.ISO_DATE_TIME))
                    .build();
        }
    }
}