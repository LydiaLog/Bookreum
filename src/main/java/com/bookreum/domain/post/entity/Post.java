package com.bookreum.domain.post.entity;

import com.bookreum.global.entity.BaseTimeEntity;
import com.bookreum.domain.user.entity.User;
import com.bookreum.domain.book.entity.Book;
import java.util.List;
import java.util.ArrayList;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
    
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostHeart> postHearts = new ArrayList<>();

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private String imageUrl;

    // ✅ 게시글 업데이트 메소드
    public void update(String title, String content, String imageUrl) {
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
    }

    // ✅ 책 업데이트 메소드 추가
    public void updateBook(Book book) {
        this.book = book;
    }

    // ✅ 직접 변경 가능한 메소드 유지
    public void setTitleDirect(String title) {
        this.title = title;
    }

    public void setContentDirect(String content) {
        this.content = content;
    }

    public void setImageUrlDirect(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}