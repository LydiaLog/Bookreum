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
public interface ClubApplicationRepository extends JpaRepository<ClubApplicationEntity, Long> {

    /**
     * ✅ 특정 사용자가 특정 클럽에 신청했는지 여부 확인
     * @param club 클럽 (모임)
     * @param user 사용자
     * @return boolean 신청 여부
     */
    boolean existsByClubAndUser(ClubEntity club, UserEntity user);
    
    /**
     * ✅ 특정 사용자의 특정 클럽 신청 내역 삭제
     * @param club 클럽 (모임)
     * @param user 사용자
     */
    void deleteByClubAndUser(ClubEntity club, UserEntity user);

    /**
     * ✅ 특정 클럽에 대한 모든 신청 내역 조회
     * @param club 클럽 (모임)
     * @return List<ClubApplicationEntity> 신청 목록
     */
    List<ClubApplicationEntity> findByClub(ClubEntity club);

    /**
     * ✅ 특정 클럽의 총 신청자 수 조회
     * - 최대 참가 인원 확인에 사용
     * @param club 클럽 (모임)
     * @return long 현재 신청된 사용자 수
     */
    long countByClub(ClubEntity club);
}
