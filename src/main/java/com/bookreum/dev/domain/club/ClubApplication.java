package com.bookreum.dev.domain.club;

import java.time.LocalDateTime;

import com.bookreum.dev.domain.user.UserEntity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 사용자의 북클럽 신청 정보를 저장하는 엔티티
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "club_application")
public class ClubApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "club_template_id", nullable = false)
    private ClubEntity club;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    private LocalDateTime appliedAt;

    @PrePersist
    protected void onCreate() {
        this.appliedAt = LocalDateTime.now();
    }
}

