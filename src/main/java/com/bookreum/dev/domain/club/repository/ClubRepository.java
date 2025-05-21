package com.bookreum.dev.domain.club.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bookreum.dev.domain.club.entity.ClubEntity;
import com.bookreum.dev.domain.club.entity.ClubStatus;

@Repository
public interface ClubRepository extends JpaRepository<ClubEntity, Integer> {
    /**
     * 키워드 검색 (제목·설명) + 최신순 정렬 (페이징)
     */
    @Query("SELECT c FROM ClubEntity c " +
           "WHERE LOWER(c.title)       LIKE LOWER(CONCAT('%', :kw, '%')) " +
           "   OR LOWER(c.description) LIKE LOWER(CONCAT('%', :kw, '%')) " +
           "ORDER BY c.createdAt DESC")
    Page<ClubEntity> searchByKeywordOrderByLatest(
        @Param("kw") String keyword,
        Pageable pageable
    );

    /**
     * 키워드 검색 (제목·설명) + 오래된순 정렬 (페이징)
     */
    @Query("SELECT c FROM ClubEntity c " +
           "WHERE LOWER(c.title)       LIKE LOWER(CONCAT('%', :kw, '%')) " +
           "   OR LOWER(c.description) LIKE LOWER(CONCAT('%', :kw, '%')) " +
           "ORDER BY c.createdAt ASC")
    Page<ClubEntity> searchByKeywordOrderByOldest(
        @Param("kw") String keyword,
        Pageable pageable
    );

    /**
     * 모든 북클럽 조회 (최신순, 책 정보 포함)
     */
    @Query("SELECT DISTINCT c FROM ClubEntity c " +
            "LEFT JOIN FETCH c.book " +
            "LEFT JOIN FETCH c.user " +
            "WHERE c.book IS NOT NULL " +
            "ORDER BY c.createdAt DESC")
     Page<ClubEntity> findAllWithBook(Pageable pageable);

    /**
     * 모든 북클럽 조회 (오래된순, 책 정보 포함)
     */
    @Query("SELECT DISTINCT c FROM ClubEntity c " +
           "LEFT JOIN FETCH c.book " +
           "LEFT JOIN FETCH c.user " +
           "WHERE c.book IS NOT NULL " +
           "ORDER BY c.createdAt ASC")
    Page<ClubEntity> findAllWithBookOldest(Pageable pageable);

    /**
     * 최신 북클럽 조회 (책 정보 포함)
     */
    @Query("SELECT DISTINCT c FROM ClubEntity c " +
           "LEFT JOIN FETCH c.book " +
           "LEFT JOIN FETCH c.user " +
           "WHERE c.book IS NOT NULL " +
           "ORDER BY c.createdAt DESC")
    Page<ClubEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);

    /**
     * 키워드 검색 (제목·설명) + 최신순 정렬 (페이징, 책 정보 포함)
     */
    @Query("SELECT DISTINCT c FROM ClubEntity c " +
           "LEFT JOIN FETCH c.book " +
           "LEFT JOIN FETCH c.user " +
           "WHERE (LOWER(c.title)       LIKE LOWER(CONCAT('%', :kw, '%')) " +
           "   OR LOWER(c.description) LIKE LOWER(CONCAT('%', :kw, '%'))) " +
           "   AND c.book IS NOT NULL " +
           "ORDER BY c.createdAt DESC")
    Page<ClubEntity> searchByKeywordOrderByLatestWithBook(
        @Param("kw") String keyword,
        Pageable pageable
    );

    /**
     * 키워드 검색 (제목·설명) + 오래된순 정렬 (페이징, 책 정보 포함)
     */
    @Query("SELECT DISTINCT c FROM ClubEntity c " +
           "LEFT JOIN FETCH c.book " +
           "LEFT JOIN FETCH c.user " +
           "WHERE (LOWER(c.title)       LIKE LOWER(CONCAT('%', :kw, '%')) " +
           "   OR LOWER(c.description) LIKE LOWER(CONCAT('%', :kw, '%'))) " +
           "   AND c.book IS NOT NULL " +
           "ORDER BY c.createdAt ASC")
    Page<ClubEntity> searchByKeywordOrderByOldestWithBook(
        @Param("kw") String keyword,
        Pageable pageable
    );

    /**
     * 마감된 북클럽 조회
     */
    @Query("SELECT c FROM ClubEntity c " +
           "WHERE c.status = 'CLOSED' " +
           "ORDER BY c.createdAt DESC")
    List<ClubEntity> findAllClosedClubs();

    /**
     * 마감된 북클럽 조회 (페이징)
     */
    @Query("SELECT c FROM ClubEntity c " +
           "WHERE c.status = 'CLOSED' " +
           "ORDER BY c.createdAt DESC")
    Page<ClubEntity> findAllClosedClubs(Pageable pageable);

    /**
     * 마감된 북클럽 조회 (책 정보 포함)
     */
    @Query("SELECT DISTINCT c FROM ClubEntity c " +
           "LEFT JOIN FETCH c.book " +
           "LEFT JOIN FETCH c.user " +
           "WHERE c.status = 'CLOSED' " +
           "ORDER BY c.createdAt DESC")
    Page<ClubEntity> findAllClosedClubsWithBook(Pageable pageable);

    /**
     * 모든 북클럽 조회 (최신순, 책 정보 포함, 상태 필터링)
     */
    @Query("SELECT DISTINCT c FROM ClubEntity c " +
           "LEFT JOIN FETCH c.book " +
           "LEFT JOIN FETCH c.user " +
           "WHERE c.book IS NOT NULL " +
           "AND (:status IS NULL OR c.status = :status) " +
           "ORDER BY c.createdAt DESC")
    Page<ClubEntity> findAllWithBookAndStatus(@Param("status") ClubStatus status, Pageable pageable);

    /**
     * 모든 북클럽 조회 (오래된순, 책 정보 포함, 상태 필터링)
     */
    @Query("SELECT DISTINCT c FROM ClubEntity c " +
           "LEFT JOIN FETCH c.book " +
           "LEFT JOIN FETCH c.user " +
           "WHERE c.book IS NOT NULL " +
           "AND (:status IS NULL OR c.status = :status) " +
           "ORDER BY c.createdAt ASC")
    Page<ClubEntity> findAllWithBookOldestAndStatus(@Param("status") ClubStatus status, Pageable pageable);

    /**
     * 키워드 검색 (제목·설명) + 최신순 정렬 (페이징, 책 정보 포함, 상태 필터링)
     */
    @Query("SELECT DISTINCT c FROM ClubEntity c " +
           "LEFT JOIN FETCH c.book " +
           "LEFT JOIN FETCH c.user " +
           "WHERE (LOWER(c.title)       LIKE LOWER(CONCAT('%', :kw, '%')) " +
           "   OR LOWER(c.description) LIKE LOWER(CONCAT('%', :kw, '%'))) " +
           "   AND c.book IS NOT NULL " +
           "   AND (:status IS NULL OR c.status = :status) " +
           "ORDER BY c.createdAt DESC")
    Page<ClubEntity> searchByKeywordOrderByLatestWithBookAndStatus(
        @Param("kw") String keyword,
        @Param("status") ClubStatus status,
        Pageable pageable
    );

    /**
     * 키워드 검색 (제목·설명) + 오래된순 정렬 (페이징, 책 정보 포함, 상태 필터링)
     */
    @Query("SELECT DISTINCT c FROM ClubEntity c " +
           "LEFT JOIN FETCH c.book " +
           "LEFT JOIN FETCH c.user " +
           "WHERE (LOWER(c.title)       LIKE LOWER(CONCAT('%', :kw, '%')) " +
           "   OR LOWER(c.description) LIKE LOWER(CONCAT('%', :kw, '%'))) " +
           "   AND c.book IS NOT NULL " +
           "   AND (:status IS NULL OR c.status = :status) " +
           "ORDER BY c.createdAt ASC")
    Page<ClubEntity> searchByKeywordOrderByOldestWithBookAndStatus(
        @Param("kw") String keyword,
        @Param("status") ClubStatus status,
        Pageable pageable
    );
    
    /**
     * 북클럽 ID로 조회 (책 정보 포함)
     */
    @Query("SELECT DISTINCT c FROM ClubEntity c " +
           "LEFT JOIN FETCH c.book " +
           "LEFT JOIN FETCH c.user " +
           "WHERE c.id = :id")
    Optional<ClubEntity> findByIdWithBook(@Param("id") Integer id);
}

}