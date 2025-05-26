package com.bookreum.dev.domain.club.entity;

import java.time.LocalDateTime;
import com.bookreum.dev.domain.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 북클럽 신청 정보 엔티티
 */
@Entity
@Table(
    name = "club_application",
    uniqueConstraints = @UniqueConstraint(columnNames = {"club_id", "user_id"})
)
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
public class ClubApplicationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false)
    private ClubEntity club;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "applied_at", nullable = false, updatable = false)
    private LocalDateTime appliedAt;

    @PrePersist
    private void onCreate() {
        this.appliedAt = LocalDateTime.now();
    }
}
