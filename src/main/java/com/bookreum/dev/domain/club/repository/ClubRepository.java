package com.bookreum.dev.domain.club.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bookreum.dev.domain.club.entity.ClubEntity;
import com.bookreum.dev.domain.club.entity.ClubStatus;

@Repository
public interface  ClubRepository extends JpaRepository<ClubEntity, Integer> {
	// ... existing code ...
    /**
     * 클럽 목록 조회 시 책 정보를 함께 가져옵니다.
     */
    @Query("SELECT DISTINCT c FROM ClubEntity c LEFT JOIN FETCH c.book LEFT JOIN FETCH c.user WHERE c.book IS NOT NULL")
    Page<ClubEntity> findAllWithBook(Pageable pageable);

    /**
     * 키워드 검색 시 책 정보를 함께 가져옵니다.
     */
    @Query("SELECT DISTINCT c FROM ClubEntity c LEFT JOIN FETCH c.book LEFT JOIN FETCH c.user " +
           "WHERE (LOWER(c.title) LIKE LOWER(CONCAT('%', :kw, '%')) " +
           "   OR LOWER(c.description) LIKE LOWER(CONCAT('%', :kw, '%'))) " +
           "   AND c.book IS NOT NULL " +
           "ORDER BY c.createdAt DESC")
    Page<ClubEntity> searchByKeywordOrderByLatestWithBook(
        @Param("kw") String keyword,
        Pageable pageable
    );

    /**
     * 키워드 검색 시 책 정보를 함께 가져옵니다 (오래된순).
     */
    @Query("SELECT DISTINCT c FROM ClubEntity c LEFT JOIN FETCH c.book LEFT JOIN FETCH c.user " +
           "WHERE (LOWER(c.title) LIKE LOWER(CONCAT('%', :kw, '%')) " +
           "   OR LOWER(c.description) LIKE LOWER(CONCAT('%', :kw, '%'))) " +
           "   AND c.book IS NOT NULL " +
           "ORDER BY c.createdAt ASC")
    Page<ClubEntity> searchByKeywordOrderByOldestWithBook(
        @Param("kw") String keyword,
        Pageable pageable
    );
    /**
     * 마감일이 지난 OPEN 상태의 클럽을 찾습니다.
     */
    List<ClubEntity> findByStatusAndApplicationDeadlineBefore(
        ClubStatus status, LocalDateTime deadline);

}
