package com.bookreum.domain.post.service;

import com.bookreum.domain.book.entity.Book;
import com.bookreum.domain.post.dto.CommentDto;
import com.bookreum.domain.post.dto.PostDto;
import com.bookreum.domain.post.entity.Post;
import com.bookreum.domain.post.repository.CommentRepository;
import com.bookreum.domain.post.repository.PostHeartRepository;
import com.bookreum.domain.post.repository.PostRepository;
import com.bookreum.domain.user.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostHeartRepository postHeartRepository;

    @Transactional
    public PostDto.Response createPost(PostDto.Request request, User user, Book book) {
        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .imageUrl(request.getImageUrl())
                .user(user)
                .book(book)
                .build();

        Post saved = postRepository.save(post);
        return PostDto.Response.fromEntity(saved, user, 0L, 0L);
    }

    public List<PostDto.Response> getAllPosts(User viewer) {
        return postRepository.findAll().stream()
                .map(post -> {
                    long heartCount = postHeartRepository.countByPost(post);
                    long commentCount = commentRepository.countByPost(post);
                    return PostDto.Response.fromEntity(post, viewer, heartCount, commentCount);
                })
                .collect(Collectors.toList());
    }

    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id);
    }

    @Transactional
    public void updatePost(Long id, PostDto.Request request) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        post.update(request.getTitle(), request.getContent(), request.getImageUrl());
    }

    @Transactional
    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    public PostDto.DetailResponse getPostDetail(Long postId, User viewer) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 글이 없습니다."));

        List<CommentDto.Response> comments = commentRepository.findByPostId(post.getId())
                .stream()
                .map(CommentDto.Response::fromEntity)
                .collect(Collectors.toList());

        long heartCount = postHeartRepository.countByPost(post);

        return PostDto.DetailResponse.fromEntity(post, comments, heartCount, viewer);
    }

    public List<PostDto.Response> getPostsSorted(String sort, User viewer) {
        List<Post> posts = switch (sort) {
            case "oldest" -> postRepository.findAllByOrderByCreatedAtAsc();
            default -> postRepository.findAllByOrderByCreatedAtDesc();
        };

        return posts.stream()
                .map(post -> {
                    long heartCount = postHeartRepository.countByPost(post);
                    long commentCount = commentRepository.countByPost(post);
                    return PostDto.Response.fromEntity(post, viewer, heartCount, commentCount);
                })
                .collect(Collectors.toList());
    }

    public List<PostDto.Response> searchPosts(String keyword, String sort, User viewer) {
        List<Post> posts = switch (sort) {
            case "oldest" -> postRepository.searchByKeywordOrderByOldest(keyword);
            default -> postRepository.searchByKeywordOrderByLatest(keyword);
        };

        return posts.stream()
                .map(post -> {
                    long heartCount = postHeartRepository.countByPost(post);
                    long commentCount = commentRepository.countByPost(post);
                    return PostDto.Response.fromEntity(post, viewer, heartCount, commentCount);
                })
                .collect(Collectors.toList());
    }
}
