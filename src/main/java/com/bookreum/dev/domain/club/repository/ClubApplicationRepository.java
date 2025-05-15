package com.bookreum.dev.domain.club.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
