// com/bookreum/dev/domain/club/ClubEntity.java
package com.bookreum.dev.domain.club;

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
    private int maxParticipants = 5;
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
