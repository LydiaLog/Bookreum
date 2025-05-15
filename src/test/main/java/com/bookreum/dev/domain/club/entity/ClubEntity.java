package com.bookreum.dev.domain.club.entity;

import java.time.LocalDateTime;
import com.bookreum.dev.domain.book.BookEntity;
import com.bookreum.dev.domain.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "club")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ClubEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private int minParticipants = 2;
    
    // ✅ 최대 참가 인원을 null 가능하도록 Integer로 수정
    private Integer maxParticipants = 5;  // 기본값을 지정할 수도 있음

    private LocalDateTime applicationDeadline;
    private int activityDurationDays;

    @Enumerated(EnumType.STRING)
    private ClubStatus status = ClubStatus.OPEN;

    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne @JoinColumn(name = "book_id")
    private BookEntity book;

    /** 모임 생성자(만든 사람) */
    @ManyToOne @JoinColumn(name = "created_by_user_id")
    private UserEntity user;
}
