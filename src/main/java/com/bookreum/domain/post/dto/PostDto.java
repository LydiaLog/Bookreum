package com.bookreum.domain.post.dto;

import com.bookreum.domain.post.entity.Post;
import com.bookreum.domain.user.entity.User;
import java.time.format.DateTimeFormatter;
import lombok.*;

import java.util.List;

public class PostDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        private String title;
        private String content;
        private String imageUrl;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Response {
        private Integer id;
        private String title;
        private String content;
        private String imageUrl;
        private String authorName;
        private String bookTitle;
        private String bookAuthor;
        private String bookCoverImageUrl;
        private String createdAt; // 생성 시간 추가
        private Long heartCount;
        private Long commentCount;

        public static Response fromEntity(Post post, User viewer, Long heartCount, Long commentCount) {
            boolean isAuthor = (viewer != null && post.getUser().getId().equals(viewer.getId()));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            return Response.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .imageUrl(post.getImageUrl())
                    .authorName(post.getUser().getNickname())
                    .bookTitle(post.getBook().getTitle())
                    .bookAuthor(post.getBook().getAuthor())
                    .bookCoverImageUrl(post.getBook().getCoverImageUrl())
                    .createdAt(post.getCreatedAt().format(formatter)) // 날짜만 표시
                    .heartCount(isAuthor ? heartCount : null)
                    .commentCount(isAuthor ? commentCount : null)
                    .build();
        }
    }


    @Getter
    @Builder
    public static class DetailResponse {
        private Integer id;
        private String title;
        private String content;
        private String imageUrl;
        private String authorName;
        private String bookTitle;
        private String bookAuthor;
        private String bookCoverImageUrl;

        private Long heartCount;
        private List<CommentDto.Response> comments;

        public static DetailResponse fromEntity(Post post, List<CommentDto.Response> comments, long heartCount, User viewer) {
            boolean isAuthor = (viewer != null && post.getUser().getId().equals(viewer.getId()));

            return DetailResponse.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .imageUrl(post.getImageUrl())
                    .authorName(post.getUser().getNickname())
                    .bookTitle(post.getBook().getTitle())
                    .bookAuthor(post.getBook().getAuthor())
                    .bookCoverImageUrl(post.getBook().getCoverImageUrl())
                    .heartCount(isAuthor ? heartCount : null)
                    .comments(comments)
                    .build();
        }
    }
    @Getter
    @Builder
    public static class SimpleResponse {
        private Integer id;
        private String title;
        private String content;
        private String date;
        private String bookTitle;
        private String bookAuthor;
        private String coverUrl;
        private String nickname;
    }

}
