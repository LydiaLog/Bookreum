package com.bookreum.dev.domain.user;


import jakarta.persistence.*;
import lombok.*;
import com.bookreum.dev.domain.post.entity.PostEntity;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserEntity {

    /**
     * 자동 생성되는 기본 키
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 카카오 고유 ID (유니크, 필수)
     */
    @Column(length = 100, unique = true, nullable = false)
    private String kakaoId;

    /**
     * 사용자 닉네임 (필수)
     */
    @Column(length = 100, nullable = false)
    private String nickname;

    /**
     * 프로필 이미지 URL (선택)
     */
    @Column(length = 255)
    private String profileImage;
    
    // 모든 사용자가 소셜 가입
    @Builder.Default
    private boolean social = true;

  
    /**
     * 카카오 프로필 변경 시 닉네임·이미지를 동기화한다.
     */
    public void updateProfile(String nickname, String profileImage) {
        this.nickname     = nickname;
        this.profileImage = profileImage;
    }


    /**
     * 사용자가 작성한 게시글 목록
     * cascade + orphanRemoval로 부모 삭제 시 연관 엔티티도 함께 관리
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PostEntity> posts = new ArrayList<>();

    /**
     * 양방향 연관관계 편의 메서드: 포스트 추가
     */
    public void addPost(PostEntity post) {
        posts.add(post);
        post.setUser(this);
    }

    /**
     * 양방향 연관관계 편의 메서드: 포스트 삭제
     */
    public void removePost(PostEntity post) {
        posts.remove(post);
        post.setUser(null);
    }
}
