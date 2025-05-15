package com.bookreum.dev.domain.user;

import jakarta.persistence.*;
import lombok.*;
import com.bookreum.dev.domain.post.entity.PostEntity;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true) // ✅ toBuilder 추가
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    @Column(length = 100, unique = true)
    private String kakaoId;

    @Column(length = 100)
    private String nickname;

    @Column(length = 255)
    private String profileImage;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostEntity> posts;
}