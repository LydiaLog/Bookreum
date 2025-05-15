package com.bookreum.dev.domain.post.service;

import com.bookreum.dev.domain.book.BookEntity;
import com.bookreum.dev.domain.book.BookRepository;
import com.bookreum.dev.domain.post.dto.PostDto;
import com.bookreum.dev.domain.post.entity.PostEntity;
import com.bookreum.dev.domain.post.repository.PostRepository;
import com.bookreum.dev.domain.user.UserEntity;
import com.bookreum.dev.domain.book.AladinBookService;
import com.bookreum.dev.domain.book.AladinItem;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@CrossOrigin(origins = "*") 
public class PostService {

    private final PostRepository postRepository;
    private final BookRepository bookRepository;
    private final AladinBookService aladinBookService;
    private static final String UPLOAD_DIR = "D:/uploads/";

    // ✅ 파일 업로드 경로 설정 (application.properties에서 설정)
    @Value("${spring.servlet.multipart.location}")
    private String uploadDir;

    /**
     * ✅ 게시글 생성 (사용자가 선택한 책 ID로)
     */
    @Transactional
    public PostDto.Response createPost(String title, String content, String coverImageUrl, MultipartFile image, UserEntity user, BookEntity book) {
        String uploadedImageUrl = image != null ? saveImage(image) : coverImageUrl;
        PostEntity post = PostEntity.builder()
                .title(title)
                .content(content)
                .imageUrl(uploadedImageUrl)
                .user(user)
                .book(book)
                .heartCount(0L)
                .build();
        PostEntity savedPost = postRepository.save(post);
        return PostDto.Response.fromEntity(savedPost, user, 0L, 0L);
    }

    /**
     * ✅ 사용자 선택 책 저장
     */
    @Transactional
    public BookEntity saveSelectedBook(AladinItem selectedItem) {
        return bookRepository.findByTitleAndAuthor(selectedItem.getTitle(), selectedItem.getAuthor())
                .orElseGet(() -> {
                    BookEntity newBook = BookEntity.builder()
                            .title(selectedItem.getTitle())
                            .author(selectedItem.getAuthor())
                            .coverImageUrl(selectedItem.getCover())
                            .build();
                    return bookRepository.save(newBook);
                });
    }

    /**
     * ✅ 이미지 저장 (서버 외부 경로 및 CORS 허용)
     */
    private String saveImage(MultipartFile image) {
        if (image == null || image.isEmpty()) return null;

        try {
            // 저장 디렉토리 생성 (서버 외부 경로)
            Path directory = Paths.get(UPLOAD_DIR).toAbsolutePath().normalize();
            Files.createDirectories(directory);

            // 파일 이름 생성 (UUID 기반, 원본 확장자 유지)
            String originalFileName = image.getOriginalFilename();
            String extension = originalFileName.substring(originalFileName.lastIndexOf('.'));
            String uniqueFileName = UUID.randomUUID().toString() + extension;

            // 파일 저장 경로 설정
            Path filePath = directory.resolve(uniqueFileName);
            image.transferTo(filePath.toFile());

            System.out.println("✅ 이미지 저장 경로: " + filePath.toString());
            // 상대 경로 반환 (프론트에서 접근 가능)
            return "http://10.50.234.11/uploads/" + uniqueFileName;

        } catch (IOException e) {
            throw new RuntimeException("이미지 저장에 실패했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * ✅ 커버 이미지 URL 결정
     */
    public String determineCoverImageUrl(MultipartFile image, String coverUrl, BookEntity book) {
        if (image != null && !image.isEmpty()) return saveImage(image);
        if (coverUrl != null && !coverUrl.isEmpty()) return coverUrl;
        return book.getCoverImageUrl();
    }

    /**
     * ✅ 게시글 수정
     */
    @Transactional
    public void updatePost(Integer id, String title, String content, Integer bookId, MultipartFile image) {
        PostEntity post = postRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post with ID " + id + " not found"));

        String imageUrl = (image != null && !image.isEmpty()) ? saveImage(image) : post.getImageUrl();

        if (bookId != null) {
            BookEntity book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book with ID " + bookId + " not found"));
            post.updateBook(book);
        }

        post.update(title, content, imageUrl);
        postRepository.save(post);
    }

    /**
     * ✅ 게시글 삭제
     */
    @Transactional
    public void deletePost(Integer id) {
        postRepository.deleteById(id);
    }

    /**
     * ✅ 페이지네이션이 적용된 최신 글 조회
     */
    public List<PostDto.Response> getLatestPosts(Pageable pageable) {
        return postRepository.findAllByOrderByCreatedAtDesc(pageable)
                .stream()
                .map(post -> PostDto.Response.fromEntity(post, null, 0L, 0L))
                .collect(Collectors.toList());
    }

    /**
     * ✅ 페이지네이션이 적용된 오래된 글 조회
     */
    public List<PostDto.Response> getOldestPosts(Pageable pageable) {
        return postRepository.findAllByOrderByCreatedAtAsc(pageable)
                .stream()
                .map(post -> PostDto.Response.fromEntity(post, null, 0L, 0L))
                .collect(Collectors.toList());
    }

    /**
     * ✅ 페이지네이션이 적용된 검색 결과 조회
     */
    public List<PostDto.Response> searchPosts(String keyword, String sort, Pageable pageable) {
        Page<PostEntity> posts = keyword == null || keyword.isBlank() 
                ? postRepository.findAll(pageable)
                : (sort.equalsIgnoreCase("latest") ? 
                   postRepository.searchByKeywordOrderByLatest(keyword, pageable) :
                   postRepository.searchByKeywordOrderByOldest(keyword, pageable));

        return posts.stream()
                .map(post -> PostDto.Response.fromEntity(post, null, 0L, 0L))
                .collect(Collectors.toList());
    }

    /**
     * ✅ 게시글 상세 조회 (postId 기반)
     */
    public PostDto.DetailResponse getPostDetail(Integer postId) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, 
                        "Post with ID " + postId + " does not exist in the database."
                ));

        System.out.println("📌 Retrieved Post: " + post);

        // 📌 PostDto.DetailResponse 생성
        return PostDto.DetailResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .imageUrl(post.getImageUrl())
                .nickname(post.getUser() != null ? post.getUser().getNickname() : "Unknown User") // ✅ 작성자 닉네임
                .date(post.getCreatedAt() != null ? post.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "Unknown Date") // ✅ 작성일
                .bookTitle(post.getBook() != null ? post.getBook().getTitle() : "Unknown Book Title") // ✅ 책 제목
                .bookAuthor(post.getBook() != null ? post.getBook().getAuthor() : "Unknown Author") // ✅ 책 저자
                .CoverUrl(post.getBook() != null ? post.getBook().getCoverImageUrl() : null) // ✅ 책 이미지 URL
                .build();
    }


    /**
     * ✅ 내가 작성한 글인지 확인
     */
    @Transactional(readOnly = true)
    public boolean isMyPost(Integer postId, UserEntity user) {
        return postRepository.findById(postId)
                .map(post -> post.getUser().getUserId().equals(user.getUserId()))
                .orElse(false);
    }
}