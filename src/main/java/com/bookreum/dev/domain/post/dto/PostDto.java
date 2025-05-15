package com.bookreum.dev.domain.post.dto;

import com.bookreum.dev.domain.post.entity.PostEntity;
import com.bookreum.dev.domain.user.UserEntity;
import java.time.format.DateTimeFormatter;
import lombok.*;
import java.util.List;

public class PostDto {

    /**
     * 게시글 생성/수정을 위한 Request DTO
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        private String title;
        private String content;
        private String imageUrl;
        private Integer bookId; 
    }

    /**
     * 게시글 목록 및 일반 응답용 Response DTO
     */
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
        private Integer bookId; 
        private String createdAt;
        private Long heartCount;
        private Long commentCount;

        /**
         * PostEntity로부터 Response 생성
         * @param post 조회된 PostEntity
         * @param viewer 현재 조회자 (authorName 용도)
         * @param heartCount 좋아요 수
         * @param commentCount 댓글 수
         */
        public static Response fromEntity(
                PostEntity post,
                UserEntity viewer,
                Long heartCount,
                Long commentCount
        ) {
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
                    .bookId(post.getBook().getId())
                    .createdAt(post.getCreatedAt().format(formatter))
                    .heartCount(heartCount != null ? heartCount : 0L)
                    .commentCount(commentCount != null ? commentCount : 0L)
                    .build();
        }
    }

    /**
     * 게시글 상세 조회용 DetailResponse DTO
     */
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
        private Integer bookId; 
        private String createdAt;
        private Long heartCount;
        private List<CommentDto.Response> comments;

        /**
         * PostEntity와 댓글 목록, 좋아요 수로부터 DetailResponse 생성
         */
        public static DetailResponse fromEntity(
                PostEntity post, List<CommentDto.Response> comments, long heartCount, UserEntity viewer
            ) {
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                return DetailResponse.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .imageUrl(post.getImageUrl())
                    .authorName(post.getUser().getNickname())
                    .bookTitle(post.getBook() != null ? post.getBook().getTitle() : "")
                    .bookAuthor(post.getBook() != null ? post.getBook().getAuthor() : "")
                    .bookCoverImageUrl(post.getBook() != null ? post.getBook().getCoverImageUrl() : null)
                    .bookId(post.getBook() != null ? post.getBook().getId() : null)
                    .createdAt(post.getCreatedAt().format(fmt))
                    .heartCount(heartCount)
                    .comments(comments)
                    .build();
            }
    }

    /**
     * 카드 형식으로 요약된 간략 정보 제공 DTO
     */
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