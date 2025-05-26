package com.bookreum.dev.domain.club.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.bookreum.dev.domain.book.BookEntity;
import com.bookreum.dev.domain.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 북클럽 모임 정보 엔티티
 */
@Entity
@Table(name = "club")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
public class ClubEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;
    private String description;
    private Integer minParticipants = 1;
    private Integer maxParticipants = 5;

    @Column(name = "application_deadline", nullable = false)
    private LocalDateTime applicationDeadline;

    private int activityDurationDays;

    @Enumerated(EnumType.STRING)
    private ClubStatus status = ClubStatus.OPEN;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @PrePersist
    private void onCreate() { this.createdAt = LocalDateTime.now(); }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private BookEntity book;

    /** 모임 생성자(호스트) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private UserEntity user;

    /** 신청자 목록 */
    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ClubApplicationEntity> applications = new ArrayList<>();

    /** 댓글(채팅) 목록 */
    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ClubCommentEntity> comments = new ArrayList<>();

    /** 댓글 추가 편의 메서드 */
    public void addComment(ClubCommentEntity comment) {
        comments.add(comment);
        comment.setClub(this);
    }

    /** 댓글 삭제 편의 메서드 */
    public void removeComment(ClubCommentEntity comment) {
        comments.remove(comment);
        comment.setClub(null);
    }

    /** 대표 이미지 URL */
    @Column(name = "club_cover_image_url", length = 255)
    private String coverImageUrl;

    /** 상태 갱신 로직 */
    public void updateStatus(int currentParticipants) {
        boolean deadlinePassed = applicationDeadline != null &&
            LocalDateTime.now().isAfter(applicationDeadline);
        boolean full = maxParticipants != null && currentParticipants >= maxParticipants;

        if (full) {
            this.status = ClubStatus.MATCHED;
        } else if (deadlinePassed) {
            this.status = ClubStatus.CLOSED;
        } else {
            this.status = ClubStatus.OPEN;
        }
    }

    public int getCurrentParticipants() {
        return applications != null ? applications.size() : 0;
    }
}
