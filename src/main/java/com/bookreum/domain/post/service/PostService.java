package com.bookreum.domain.post.service;

import com.bookreum.domain.book.entity.Book;
import com.bookreum.domain.book.repository.BookRepository;
import com.bookreum.domain.post.dto.PostDto;
import com.bookreum.domain.post.entity.Post;
import com.bookreum.domain.post.repository.CommentRepository;
import com.bookreum.domain.post.repository.PostHeartRepository;
import com.bookreum.domain.post.repository.PostRepository;
import com.bookreum.domain.user.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostHeartRepository postHeartRepository;
    private final BookRepository bookRepository;

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";

    @Transactional
    public PostDto.Response createPost(String title, String content, MultipartFile image, User user, Book book) {
        String imageUrl = saveImage(image);

        Post post = Post.builder()
                .title(title)
                .content(content)
                .imageUrl(imageUrl)
                .user(user)
                .book(book)
                .build();

        Post saved = postRepository.save(post);
        return PostDto.Response.fromEntity(saved, user, 0L, 0L);
    }

    private String saveImage(MultipartFile image) {
        if (image == null || image.isEmpty()) return null;

        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

            String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            image.transferTo(filePath.toFile());

            return "/uploads/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save image", e);
        }
    }

    @Transactional
    public void updatePost(Long id, String title, String content, Long bookId, MultipartFile image) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        String imageUrl = saveImage(image);

        if (bookId != null) {
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new IllegalArgumentException("Book not found"));
            post.updateBook(book);
        }

        post.update(title, content, imageUrl);

        postRepository.save(post);
    }

    @Transactional
    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    public List<PostDto.Response> getLatestPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(post -> PostDto.Response.fromEntity(post, null, 0L, 0L))
                .collect(Collectors.toList());
    }

    public List<PostDto.Response> getOldestPosts() {
        return postRepository.findAllByOrderByCreatedAtAsc()
                .stream()
                .map(post -> PostDto.Response.fromEntity(post, null, 0L, 0L))
                .collect(Collectors.toList());
    }

    public List<PostDto.Response> searchPosts(String keyword, String sort, User viewer) {
        List<Post> posts = postRepository.searchByKeywordOrderByLatest(keyword);
        return posts.stream()
                .map(post -> PostDto.Response.fromEntity(post, viewer, 0L, 0L))
                .collect(Collectors.toList());
    }

    public PostDto.DetailResponse getPostDetail(Long postId, User viewer) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        return PostDto.DetailResponse.fromEntity(post, null, 0L, viewer);
    }
}
