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
@Builder
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
    private List<PostHeart> postHearts = new ArrayList<>();
    
    //✅ 공감 수 필드 추가
    @Column(name = "heart_count", nullable = false)
    @Builder.Default
    private long heartCount = 0L;
    
    //✅ 공감 수 증가
    public void incrementHeartCount() {
    	this.heartCount++;
    }
    
    //✅ 공감 수 감소
    public void decrementHeartCount() {
    	if(this.heartCount > 0) {
    		this.heartCount--;
    	}
    }
    
    //✅ 생성 시간 설정
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // ✅ updateBook 메소드 추가
    public void updateBook(BookEntity book) {
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
        incrementHeartCount();
    }

    // ✅ PostHeart 삭제
    public void removeHeart(PostHeart postHeart) {
        this.postHearts.remove(postHeart);
        postHeart.setPost(null);
        decrementHeartCount();
    }
}