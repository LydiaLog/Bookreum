package com.bookreum.domain.post.dto;

import com.bookreum.domain.post.entity.Comment;
import lombok.*;

public class CommentDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        private Long postId;
        private Long userId;
        private String content;
    }

    @Getter
    @Builder
    public static class Response {
        private Long id;
        private String content;
        private String createdAt;
        private String authorName;

        public static Response fromEntity(Comment comment) {
            return Response.builder()
                    .id(comment.getId())
                    .content(comment.getContent())
                    .createdAt(comment.getCreatedAt().toString())
                    .authorName(comment.getUser().getNickname())
                    .build();
        }
    }
}
