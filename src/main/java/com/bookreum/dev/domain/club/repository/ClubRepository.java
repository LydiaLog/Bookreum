package com.bookreum.dev.domain.club.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bookreum.dev.domain.club.entity.ClubEntity;

@Repository
public interface  ClubRepository extends JpaRepository<ClubEntity, Integer> {
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
     * 클럽 목록 조회 시 책 정보를 함께 가져옵니다.
     */
    @Query("SELECT c FROM ClubEntity c LEFT JOIN FETCH c.book")
    Page<ClubEntity> findAllWithBook(Pageable pageable);
}
