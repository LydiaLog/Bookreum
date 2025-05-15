package com.bookreum.dev.domain.post.dto;

import com.bookreum.dev.domain.post.entity.PostEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import java.time.format.DateTimeFormatter;


/**
 * MapStruct를 이용한 PostEntity ↔ DTO 매핑 인터페이스
 */
@Mapper(componentModel = "spring")
public interface PostMapper {

    DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 목록/일반 응답(Response) 매핑
     */
    @Mapping(source = "user.nickname",       target = "authorName")
    @Mapping(source = "book.title",          target = "bookTitle")
    @Mapping(source = "book.author",         target = "bookAuthor")
    @Mapping(source = "book.coverImageUrl",  target = "bookCoverImageUrl")
    @Mapping(source = "book.id",             target = "bookId")
    @Mapping(source = "createdAt",           target = "createdAt", qualifiedByName = "formatDate")
    @Mapping(target = "heartCount",          ignore = true)
    @Mapping(target = "commentCount",        ignore = true)
    PostDto.Response toResponse(PostEntity post);

    /**
     * 상세 조회(DetailResponse) 매핑
     */
    @Mapping(source = "user.nickname",       target = "authorName")
    @Mapping(source = "book.title",          target = "bookTitle")
    @Mapping(source = "book.author",         target = "bookAuthor")
    @Mapping(source = "book.coverImageUrl",  target = "bookCoverImageUrl")
    @Mapping(source = "book.id",             target = "bookId")
    @Mapping(source = "createdAt",           target = "createdAt", qualifiedByName = "formatDateTime")
    @Mapping(target = "heartCount",          ignore = true)
    @Mapping(target = "comments",            ignore = true)
    PostDto.DetailResponse toDetail(PostEntity post);

    /**
     * SimpleResponse 변환
     */
    @Mapping(source = "id",                  target = "id")
    @Mapping(source = "title",               target = "title")
    @Mapping(source = "content",             target = "content")
    @Mapping(source = "createdAt",           target = "date")
    @Mapping(source = "bookTitle",           target = "bookTitle")
    @Mapping(source = "bookAuthor",          target = "bookAuthor")
    @Mapping(source = "bookCoverImageUrl",   target = "coverUrl")
    @Mapping(source = "authorName",          target = "nickname")
    PostDto.SimpleResponse toSimple(PostDto.Response response);

    /**
     * 날짜 포맷 매핑 헬퍼
     */
    @Named("formatDate")
    default String formatDate(java.time.LocalDateTime date) {
        return date != null ? date.format(DATE_FORMAT) : null;
    }

    @Named("formatDateTime")
    default String formatDateTime(java.time.LocalDateTime date) {
        return date != null ? date.format(DATETIME_FORMAT) : null;
    }
}

