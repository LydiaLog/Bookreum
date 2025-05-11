package com.bookreum.dev.domain.user;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Table(name = "users")
public class UserEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 카카오 고유 UUID
    @Column(unique = true, nullable = false)
    private String kakaoId;

    private String nickname;
    private String profileImage;

    @Column(nullable = false)
    private boolean social;

    @Column(nullable = false)
    private boolean del;
}
