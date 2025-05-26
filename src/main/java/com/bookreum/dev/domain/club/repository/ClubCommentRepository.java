package com.bookreum.dev.domain.club.repository;


import org.springframework.data.domain.Pageable; 
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bookreum.dev.domain.club.entity.ClubCommentEntity;
import com.bookreum.dev.domain.club.entity.ClubEntity;

@Repository
public interface ClubCommentRepository extends JpaRepository<ClubCommentEntity,Integer> {
	/** 모임별 댓글 목록 조회 (작성 시각 오름차순) */
	List<ClubCommentEntity> findByClubOrderByCreatedAtAsc(ClubEntity club);

	/** 모임별 댓글 목록 조회 (작성자 정보 포함, 작성 시각 오름차순) */
    @Query("SELECT c FROM ClubCommentEntity c JOIN FETCH c.author WHERE c.club = :club ORDER BY c.createdAt ASC")
    List<ClubCommentEntity> findByClubWithAuthorOrderByCreatedAtAsc(@Param("club") ClubEntity club);
    
    /** 모임별 댓글 페이징 조회 (작성 시각 오름차순) */
    Page<ClubCommentEntity> findByClubOrderByCreatedAtAsc(ClubEntity club, Pageable pageable);
    

    /** 댓글 단건 조회 (작성자 정보 포함) */
    @Query("SELECT c FROM ClubCommentEntity c JOIN FETCH c.author WHERE c.id = ?1")
    Optional<ClubCommentEntity> findByIdWithAuthor(Integer commentId);

}

