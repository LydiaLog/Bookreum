package com.bookreum.dev.domain.club.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bookreum.dev.domain.club.entity.ClubApplicationEntity;
import com.bookreum.dev.domain.club.entity.ClubEntity;
import com.bookreum.dev.domain.user.UserEntity;

/**
 * ✅ ClubApplication CRUD 및 커스텀 쿼리 인터페이스
 */
@Repository
public interface ClubApplicationRepository extends JpaRepository<ClubApplicationEntity, Integer> {

    /**
     * 주어진 클럽과 사용자의 신청 여부를 확인합니다.
     *
     * @param club 모임 엔티티
     * @param user 사용자 엔티티
     * @return 신청이 존재하면 true, 아니면 false
     */
    boolean existsByClubAndUser(ClubEntity club, UserEntity user);

    /**
     * 주어진 클럽과 사용자의 신청 정보를 삭제합니다.
     *
     * @param club 모임 엔티티
     * @param user 사용자 엔티티
     */
    void deleteByClubAndUser(ClubEntity club, UserEntity user);

    /**
     * 특정 클럽에 신청한 모든 신청 정보를 반환합니다.
     *
     * @param club 모임 엔티티
     * @return 신청 엔티티 리스트
     */
    List<ClubApplicationEntity> findByClub(ClubEntity club);

    /**
     * 특정 클럽에 신청한 사용자 수를 반환합니다.
     *
     * @param club 모임 엔티티
     * @return 신청자 수
     */
    long countByClub(ClubEntity club);
    
    List<ClubApplicationEntity> findByUser(UserEntity user);

// // 내가 신청한 모임 목록
// @Query("""
//    SELECT cr FROM ChatRoomEntity cr
//    JOIN cr.club c
//    JOIN ClubApplicationEntity a ON a.club = c
//    WHERE a.user = :user
// """)
// List<ChatRoomEntity> findRoomsByUser(@Param("user") UserEntity user);
//}
    /**
     * 특정 모임에 신청한 사용자 엔티티 리스트만 조회
     */
    @Query("SELECT a.user FROM ClubApplicationEntity a WHERE a.club = :club")
    List<UserEntity> findUsersByClub(@Param("club") ClubEntity club);
}