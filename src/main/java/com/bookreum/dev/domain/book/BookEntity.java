package com.bookreum.dev.domain.book;

import jakarta.persistence.*;
import lombok.*;
import com.bookreum.dev.domain.post.entity.PostEntity;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@Builder
@Table(name = "book") // ✅ 테이블 명 명시 (DB의 테이블명과 매칭)
public class BookEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false) // ✅ 필수값으로 지정
    private String title;

    @Column(nullable = false) // ✅ 필수값으로 지정
    private String author;

    @Column(length = 255)
    private String coverImageUrl;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostEntity> posts;
}