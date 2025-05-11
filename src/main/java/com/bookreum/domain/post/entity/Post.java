package com.bookreum.domain.post.entity;

import com.bookreum.domain.book.entity.Book;
import com.bookreum.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(length = 255, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(length = 255)
    private String imageUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostHeart> postHearts = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // ✅ updateBook 메소드 추가
    public void updateBook(Book book) {
        this.book = book;
    }

    // ✅ update 메소드로 일반 수정
    public void update(String title, String content, String imageUrl) {
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
    }

    // ✅ PostHeart 추가
    public void addHeart(PostHeart postHeart) {
        this.postHearts.add(postHeart);
        postHeart.setPost(this);
    }

    // ✅ PostHeart 삭제
    public void removeHeart(PostHeart postHeart) {
        this.postHearts.remove(postHeart);
        postHeart.setPost(null);
    }
}
