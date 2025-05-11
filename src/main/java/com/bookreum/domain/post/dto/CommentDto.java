package com.bookreum.domain.post.dto;

import com.bookreum.domain.post.entity.Comment;
import lombok.*;

public class CommentDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        private Integer postId;
        private Integer userId;
        private String content;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Integer id;
        private String content;
        private String createdAt;
        private String authorName;
        private long heartCount;

        // ✅ Comment 엔티티에서 Response 생성 (기본)
        public static Response fromEntity(Comment comment) {
            return Response.builder()
                    .id(comment.getId())
                    .content(comment.getContent())
                    .createdAt(comment.getCreatedAt().toString())
                    .authorName(comment.getUser().getNickname())
                    .build();
        }

        // ✅ 공감 수를 포함한 Response 생성
        public static Response fromEntityWithHeartCount(Comment comment, long heartCount) {
            return Response.builder()
                    .id(comment.getId())
                    .content(comment.getContent())
                    .createdAt(comment.getCreatedAt().toString())
                    .authorName(comment.getUser().getNickname())
                    .heartCount(heartCount)
                    .build();
        }
    }
}
