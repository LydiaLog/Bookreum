package com.bookreum.domain.post.service;

import com.bookreum.domain.book.entity.Book;
import com.bookreum.domain.book.repository.BookRepository;
import com.bookreum.domain.post.dto.PostDto;
import com.bookreum.domain.post.entity.Post;
import com.bookreum.domain.post.repository.PostRepository;
import com.bookreum.domain.user.entity.User;
import com.bookreum.external.aladin.AladinBookService;
import com.bookreum.external.aladin.AladinItem;
import com.bookreum.external.aladin.AladinSearchResult;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final BookRepository bookRepository;
    private final AladinBookService aladinBookService;

    // ✅ 파일 업로드 경로 설정 (application.yml 또는 application.properties에서 설정)
    @Value("${spring.servlet.multipart.location}")
    private String uploadDir;

    @Transactional
    public PostDto.Response createPost(String title, String content, String coverImageUrl, MultipartFile image, User user, Book book) {
        String uploadedImageUrl = saveImage(image);

        Post post = Post.builder()
                .title(title)
                .content(content)
                .imageUrl(uploadedImageUrl != null ? uploadedImageUrl : coverImageUrl)
                .user(user)
                .book(book)
                .build();

        Post savedPost = postRepository.save(post);
        return PostDto.Response.fromEntity(savedPost, user, 0L, 0L);
    }

    private String saveImage(MultipartFile image) {
        if (image == null || image.isEmpty()) return null;
        try {
            // ✅ 파일 저장 경로 지정
            java.io.File dir = new java.io.File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // ✅ 저장할 파일 경로 설정
            String filePath = uploadDir + "/" + image.getOriginalFilename();
            java.io.File saveFile = new java.io.File(filePath);
            image.transferTo(saveFile);

            System.out.println("✅ 이미지 저장 경로: " + saveFile.getAbsolutePath());

            // ✅ 웹에서 접근 가능한 경로 반환 (상대 경로)
            return "/uploads/" + image.getOriginalFilename();
        } catch (IOException e) {
            throw new RuntimeException("Failed to save image", e);
        }
    }
 // ✅ 책 결정 (bookId 또는 keyword)
    public Book determineBook(Integer bookId, String keyword) {
        if (bookId != null) {
            return bookRepository.findById(bookId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book with ID " + bookId + " not found"));
        }
        if (keyword != null) {
            return searchAndSaveBookByKeyword(keyword);
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Either bookId or keyword must be provided.");
    }

    // ✅ 키워드로 책 검색 및 저장
    @Transactional
    public Book searchAndSaveBookByKeyword(String keyword) {
        AladinSearchResult result = aladinBookService.searchBooks(keyword);

        if (result.getTotalResults() > 0) {
            AladinItem aladinItem = result.getItem().get(0);
            Book existingBook = bookRepository.findByTitleAndAuthor(aladinItem.getTitle(), aladinItem.getAuthor()).orElse(null);

            if (existingBook != null) {
                return existingBook;
            }

            // ✅ 새로운 책 저장
            Book newBook = Book.builder()
                    .title(aladinItem.getTitle())
                    .author(aladinItem.getAuthor())
                    .coverImageUrl(aladinItem.getCover())
                    .build();
            return bookRepository.save(newBook);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No books found with the given keyword.");
        }
    }


    // ✅ 커버 이미지 URL 결정
    public String determineCoverImageUrl(MultipartFile image, String coverUrl, Book book) {
        if (image != null && !image.isEmpty()) return saveImage(image);
        if (coverUrl != null && !coverUrl.isEmpty()) return coverUrl;
        return book.getCoverImageUrl();
    }

    // ✅ 게시글 수정
    @Transactional
    public void updatePost(Integer id, String title, String content, Integer bookId, MultipartFile image) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        String imageUrl = saveImage(image);

        if (bookId != null) {
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new IllegalArgumentException("Book not found"));
            post.updateBook(book);
        }

        post.update(title, content, imageUrl != null ? imageUrl : post.getImageUrl());
        postRepository.save(post);
    }

    // ✅ 게시글 삭제
    @Transactional
    public void deletePost(Integer id) {
        postRepository.deleteById(id);
    }

    // ✅ 페이지네이션이 적용된 최신 글 조회
    public List<PostDto.Response> getLatestPosts(Pageable pageable) {
        Page<Post> postPage = postRepository.findAllByOrderByCreatedAtDesc(pageable);
        return postPage.stream()
                .map(post -> PostDto.Response.fromEntity(post, null, 0L, 0L))
                .collect(Collectors.toList());
    }

    // ✅ 페이지네이션이 적용된 오래된 글 조회
    public List<PostDto.Response> getOldestPosts(Pageable pageable) {
        Page<Post> postPage = postRepository.findAllByOrderByCreatedAtAsc(pageable);
        return postPage.stream()
                .map(post -> PostDto.Response.fromEntity(post, null, 0L, 0L))
                .collect(Collectors.toList());
    }

    // ✅ 페이지네이션이 적용된 검색 결과 조회
    public List<PostDto.Response> searchPosts(String keyword, String sort, Pageable pageable) {
        Page<Post> posts = sort.equalsIgnoreCase("latest") ?
                postRepository.searchByKeywordOrderByLatest(keyword, pageable) :
                postRepository.searchByKeywordOrderByOldest(keyword, pageable);

        return posts.stream()
                .map(post -> PostDto.Response.fromEntity(post, null, 0L, 0L))
                .collect(Collectors.toList());
    }

    // ✅ 게시글 상세 조회
    public PostDto.DetailResponse getPostDetail(Integer postId, User viewer) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        return PostDto.DetailResponse.fromEntity(post, null, 0L, viewer);
    }
}
