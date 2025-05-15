package com.bookreum.dev.domain.post.service;

import com.bookreum.dev.domain.book.BookEntity;
import com.bookreum.dev.domain.book.BookRepository;
import com.bookreum.dev.domain.post.dto.CommentDto;
import com.bookreum.dev.domain.post.dto.PostDto;
import com.bookreum.dev.domain.post.entity.PostEntity;
import com.bookreum.dev.domain.post.entity.PostHeart;
import com.bookreum.dev.domain.post.repository.CommentHeartRepository;
import com.bookreum.dev.domain.post.repository.CommentRepository;
import com.bookreum.dev.domain.post.repository.PostHeartRepository;
import com.bookreum.dev.domain.post.repository.PostRepository;
import com.bookreum.dev.domain.user.UserEntity;
import com.bookreum.dev.domain.user.UserRepository;
import com.bookreum.dev.domain.book.AladinBookService;
import com.bookreum.dev.domain.book.AladinItem;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 게시글 생성, 조회, 수정, 삭제, 검색, 좋아요 조회 등을
 * 처리하는 서비스 컴포넌트입니다.
 */
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;               		// 게시글 레포지토리
    private final PostHeartRepository postHeartRepository;     		// 좋아요 레포지토리
    private final CommentRepository commentRepository;   	   		// 댓글 레포지토리
    private final CommentHeartRepository commentHeartRepository; 	//댓글 좋아요 레포지토리
    private final UserRepository userRepository;               		// 사용자 레포지토리
    private final BookRepository bookRepository;               		// 책 레포지토리
    private final AladinBookService aladinBookService;         		// 외부 API 서비스

    @Value("${spring.servlet.multipart.location}")
    private String uploadDir; // 업로드 디렉터리 설정

    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * 게시글 생성
     */
    @Transactional
    public PostDto.Response createPost(
        String title,
        String content,
        String coverUrl,
        MultipartFile image,
        UserEntity user,
        BookEntity book
    ) {
        String imageUrl = determineCoverImageUrl(image, coverUrl, book);
        PostEntity post = PostEntity.builder()
                .title(title)
                .content(content)
                .imageUrl(imageUrl)
                .createdAt(LocalDateTime.now())
                .user(user)
                .book(book)
                .build();
        postRepository.save(post);
        return PostDto.Response.fromEntity(post, user, 0L, 0L);
    }

    /**
     * 사용자가 선택한 책 저장
     */
    @Transactional
    public BookEntity saveSelectedBook(AladinItem item) {
        return bookRepository.findByTitleAndAuthor(item.getTitle(), item.getAuthor())
                .orElseGet(() -> bookRepository.save(
                    BookEntity.builder()
                        .title(item.getTitle())
                        .author(item.getAuthor())
                        .coverImageUrl(item.getCover())
                        .build()
                ));
    }

    /**
     * 이미지 저장
     */
    private String saveImage(MultipartFile image) {
        if (image == null || image.isEmpty()) return null;
        try {
            Path dir = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(dir);
            String ext = image.getOriginalFilename()
                .substring(image.getOriginalFilename().lastIndexOf('.'));
            String filename = UUID.randomUUID() + ext;
            Path target = dir.resolve(filename);
            image.transferTo(target);
            return filename;
        } catch (IOException e) {
            throw new RuntimeException("이미지 저장 실패: " + e.getMessage());
        }
    }

    /**
     * 커버 이미지 URL 결정
     */
    public String determineCoverImageUrl(
        MultipartFile image,
        String coverUrl,
        BookEntity book
    ) {
        if (image != null && !image.isEmpty()) {
            return saveImage(image);
        }
        if (coverUrl != null && !coverUrl.isEmpty()) {
            return coverUrl;
        }
        return book.getCoverImageUrl();
    }

    /**
     * 게시글 수정
     */
    @Transactional
    public void updatePost(
        Integer postId,
        String title,
        String content,
        Integer bookId,
        MultipartFile image
    ) {
        PostEntity post = postRepository.findById(postId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다. id=" + postId));
        if (image != null && !image.isEmpty()) {
            post.update(title, content, saveImage(image));
        } else {
            post.update(title, content, post.getImageUrl());
        }
        if (bookId != null) {
            BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "책을 찾을 수 없습니다. id=" + bookId));
            post.updateBook(book);
        }
    }

    /**
     * 게시글 삭제
     */
    @Transactional
    public void deletePost(Integer postId) {
        postRepository.deleteById(postId);
    }

    /**
     * 최신 게시글 페이징 조회
     */
    public Page<PostDto.Response> getLatestPosts(Pageable pageable) {
        return postRepository.findAllByOrderByCreatedAtDesc(pageable)
            .map(post -> {
                long hearts = postHeartRepository.countByPostId(post.getId());
                return PostDto.Response.fromEntity(post, null, hearts, 0L);
            });
    }

    /**
     * 오래된 게시글 페이징 조회
     */
    public Page<PostDto.Response> getOldestPosts(Pageable pageable) {
        return postRepository.findAllByOrderByCreatedAtAsc(pageable)
            .map(post -> {
                long hearts = postHeartRepository.countByPostId(post.getId());
                return PostDto.Response.fromEntity(post, null, hearts, 0L);
            });
    }

    /**
     * 키워드 검색 + 정렬 + 페이징
     */
    public Page<PostDto.Response> searchPosts(
        String keyword,
        String sort,
        Pageable pageable
    ) {
        Page<PostEntity> posts = (keyword == null || keyword.isBlank())
            ? postRepository.findAll(pageable)
            : ("oldest".equalsIgnoreCase(sort)
                ? postRepository.searchByKeywordOrderByOldest(keyword, pageable)
                : postRepository.searchByKeywordOrderByLatest(keyword, pageable));
        return posts.map(post -> PostDto.Response.fromEntity(
            post, null,
            postHeartRepository.countByPostId(post.getId()),
            commentRepository.countByPostId(post.getId())
        ));
    }

    /**
     * 게시글 상세 조회
     */
    public PostDto.DetailResponse getPostDetail(Integer postId) {
        // 1) Post 찾기
        PostEntity post = postRepository.findById(postId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다. id=" + postId));

        // 2) 댓글 조회 (postId 기반)
        List<CommentDto.Response> comments = commentRepository
            .findByPostIdOrderByCreatedAtDesc(postId)    // ← 여기
            .stream()
            .map(comment -> {
                long heartCount = commentHeartRepository.countByCommentId(comment.getId());
                return CommentDto.Response.fromEntityWithHeartCount(comment, heartCount);
            })
            .collect(Collectors.toList());

        // 3) 좋아요 개수
        long hearts = postHeartRepository.countByPostId(postId);

        // 4) DTO 변환
        return PostDto.DetailResponse.fromEntity(post, comments, hearts, null);
    }


    /**
     * 특정 사용자가 좋아요 누른 게시글 조회
     */
    public Page<PostDto.Response> getLikedPosts(
        Integer userId,
        Pageable pageable
    ) {
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. id=" + userId));
        return postRepository.findPostsLikedByUser(user, pageable)
            .map(post -> PostDto.Response.fromEntity(
                post, null,
                postHeartRepository.countByPostId(post.getId()),
                commentRepository.countByPostId(post.getId())
            ));
    }

    /**
     * 내가 작성한 게시글인지 확인
     */
    @Transactional(readOnly = true)
    public boolean isMyPost(Integer postId, UserEntity user) {
        return postRepository.findById(postId)
            .map(p -> p.getUser().getId().equals(user.getId()))
            .orElse(false);
    }
}