package com.bookreum.dev.domain.post.entity;

import com.bookreum.dev.domain.book.BookEntity;
import com.bookreum.dev.domain.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
public class PostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private BookEntity book;

    @Column(length = 255, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(length = 255)
    private String imageUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PostHeart> postHearts = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    /** 연관관계 편의 메서드: 사용자 설정 */
    public void setUser(UserEntity user) {
        this.user = user;
    }

    /** 연관관계 편의 메서드: 책 변경 */
    public void updateBook(BookEntity book) {
        this.book = book;
    }

    /** 게시글 내용 수정 */
    public void update(String title, String content, String imageUrl) {
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
    }

    /** 좋아요 추가 */
    public void addHeart(PostHeart heart) {
        postHearts.add(heart);
        heart.setPost(this);
    }

    /** 좋아요 제거 */
    public void removeHeart(PostHeart heart) {
        postHearts.remove(heart);
        heart.setPost(null);
    }
}