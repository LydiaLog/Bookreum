package com.bookreum.dev.domain.club;

import java.time.LocalDateTime;

import com.bookreum.dev.domain.book.BookEntity;
import com.bookreum.dev.domain.user.UserEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "club")
public class ClubEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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

	 @ManyToOne
	 @JoinColumn(name = "book_id")
	 private BookEntity book;

	 @ManyToOne
	 @JoinColumn(name = "created_by_user_id")
	 private UserEntity user;
}
