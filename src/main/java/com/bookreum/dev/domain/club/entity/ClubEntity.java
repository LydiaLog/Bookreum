package com.bookreum.dev.domain.club.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.bookreum.dev.domain.book.BookEntity;
import com.bookreum.dev.domain.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;

/**
* 북클럽 모임 정보 엔티티
*/
@Entity
@Table(name = "club")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
public class ClubEntity {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Integer id;

   private String title;
   private String description;

   private Integer minParticipants = 1;
   private Integer maxParticipants = 5;

   @Column(name = "application_deadline", nullable = false)
   private LocalDateTime applicationDeadline;

   private int activityDurationDays;

   @Enumerated(EnumType.STRING)
   private ClubStatus status = ClubStatus.OPEN;

   @Column(name = "created_at", nullable = false, updatable = false)
   private LocalDateTime createdAt;

   @PrePersist
   private void onCreate() {
       this.createdAt = LocalDateTime.now();
   }

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "book_id", nullable = false)
   private BookEntity book;

   /** 모임 생성자 */
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "created_by_user_id", nullable = false)
   private UserEntity user;

   /** 사용자 신청 목록 */
   @OneToMany(mappedBy = "club", cascade = CascadeType.ALL, orphanRemoval = true)
   @Builder.Default
   private List<ClubApplicationEntity> applications = new ArrayList<>();

   /** 신청 추가 편의 메서드 */
   public void addApplication(ClubApplicationEntity app) {
       applications.add(app);
       app.setClub(this);
   }

   /** 신청 삭제 편의 메서드 */
   public void removeApplication(ClubApplicationEntity app) {
       applications.remove(app);
       app.setClub(null);
   }

   /** 채팅방 연관 */
   @OneToOne(mappedBy = "club", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
   private ChatRoomEntity chatRoom;
   
   /** 모임 대표 이미지 URL */
   @Column(name = "club_cover_image_url", length = 255)
   private String coverImageUrl;
}
