package com.bookreum.dev.domain.club;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.bookreum.dev.domain.user.UserEntity;

/**
 * ClubApplication CRUD 및 커스텀 쿼리 인터페이스
 */
@Repository
public interface ClubApplicationRepository extends JpaRepository<ClubApplication, Long> {

    /**
     * 특정 사용자가 특정 클럽에 신청했는지 여부
     */
    boolean existsByClubAndUser(ClubEntity club, UserEntity user);
    
    /**
     * 특정 사용자의 특정 클럽 신청 내역 삭제
     */
    void deleteByClubAndUser(ClubEntity club, UserEntity user);

    /**
     * 특정 클럽에 대한 모든 신청 내역 조회
     */
    List<ClubApplication> findByClub(ClubEntity club);
}

