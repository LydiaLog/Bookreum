package com.bookreum.domain.post.dto;

import com.bookreum.domain.post.entity.Post;
import com.bookreum.domain.user.entity.User;
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
        private Long id;
        private String title;
        private String content;
        private String imageUrl;
        private String authorName;
        private String bookTitle;
        private String bookAuthor;
        private String bookCoverImageUrl;

        // 마음, 댓글 수 작성자에게만 보이게 처리
        private Long heartCount;
        private Long commentCount;

        public static Response fromEntity(Post post, User viewer, Long heartCount, Long commentCount) {
            boolean isAuthor = post.getUser().getId().equals(viewer.getId());

            return Response.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .imageUrl(post.getImageUrl())
                    .authorName(post.getUser().getNickname())
                    .bookTitle(post.getBook().getTitle())
                    .bookAuthor(post.getBook().getAuthor())
                    .bookCoverImageUrl(post.getBook().getCoverImageUrl())
                    .heartCount(isAuthor ? heartCount : null)
                    .commentCount(isAuthor ? commentCount : null)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class DetailResponse {
        private Long id;
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
            boolean isAuthor = post.getUser().getId().equals(viewer.getId());

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
}
