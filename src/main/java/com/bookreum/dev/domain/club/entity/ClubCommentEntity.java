package com.bookreum.dev.domain.club.entity;

import java.time.LocalDateTime;
import com.bookreum.dev.domain.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 모임에 달리는 댓글 정보를 저장하는 엔티티
 */
@Entity
@Table(name = "club_comment")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ClubCommentEntity {

    /** 댓글 고유 ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** 댓글이 달린 모임 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false)
    private ClubEntity club;

    /** 댓글 작성자 (UserEntity) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity author;

    /** 댓글 내용 */
    @Column(nullable = false, length = 1000)
    private String content;

    /** 댓글 작성 시각 (자동 설정) */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

