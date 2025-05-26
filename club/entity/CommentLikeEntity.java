package com.bookreum.dev.domain.club.entity;

import com.bookreum.dev.domain.user.UserEntity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "comment_like",
    uniqueConstraints = @UniqueConstraint(columnNames = {"comment_id", "user_id"})
)
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentLikeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private ClubCommentEntity comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
}
