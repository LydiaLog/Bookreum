package com.bookreum.dev.domain.post.entity;

import com.bookreum.dev.domain.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

//Comment 엔티티
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CommentEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id", nullable = false)
	private PostEntity post;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private UserEntity user;

	@Column(columnDefinition = "TEXT", nullable = false)
	private String content;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@PrePersist
	public void onCreate() {
		this.createdAt = LocalDateTime.now();
	}

	// ✅ 안전한 Setter 메소드 추가 (보호된 접근)
	public void setContent(String content) {
		this.content = content;
	}

	// ✅ 내용 업데이트 메소드 (권장)
	public void updateContent(String newContent) {
		this.content = newContent;
	}
}