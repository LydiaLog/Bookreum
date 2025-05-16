package com.bookreum.dev.domain.post.repository;

import com.bookreum.dev.domain.post.entity.PostEntity;
import com.bookreum.dev.domain.user.UserEntity;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 게시글 조회를 위한 JPA 레포지토리
 */
@Repository
public interface PostRepository extends JpaRepository<PostEntity, Integer> {

    /** 
     * 최신순 게시글 전체 조회 (페이징) 
     */
    Page<PostEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);

    /** 
     * 오래된순 게시글 전체 조회 (페이징) 
     */
    Page<PostEntity> findAllByOrderByCreatedAtAsc(Pageable pageable);

    /**
     * 키워드 검색 (제목·본문·책 제목·저자) + 최신순 정렬 (페이징)
     */
    @Query("SELECT p FROM PostEntity p JOIN p.book b " +
           "WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :kw, '%')) " +
           "   OR LOWER(p.content) LIKE LOWER(CONCAT('%', :kw, '%')) " +       // 본문 포함
           "   OR LOWER(b.title) LIKE LOWER(CONCAT('%', :kw, '%')) " +
           "   OR LOWER(b.author) LIKE LOWER(CONCAT('%', :kw, '%')) " +
           "ORDER BY p.createdAt DESC")
    Page<PostEntity> searchByKeywordOrderByLatest(
        @Param("kw") String keyword,
        Pageable pageable
    );

    /**
     * 키워드 검색 (제목·본문·책 제목·저자) + 오래된순 정렬 (페이징)
     */
    @Query("SELECT p FROM PostEntity p JOIN p.book b " +
           "WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :kw, '%')) " +
           "   OR LOWER(p.content) LIKE LOWER(CONCAT('%', :kw, '%')) " +       // 본문 포함
           "   OR LOWER(b.title) LIKE LOWER(CONCAT('%', :kw, '%')) " +
           "   OR LOWER(b.author) LIKE LOWER(CONCAT('%', :kw, '%')) " +
           "ORDER BY p.createdAt ASC")
    Page<PostEntity> searchByKeywordOrderByOldest(
        @Param("kw") String keyword,
        Pageable pageable
    );


    /**
     * 특정 사용자가 작성한 게시글 조회 (최신순, 페이징)
     */
    Page<PostEntity> findByUserOrderByCreatedAtDesc(
        UserEntity user,
        Pageable pageable
    );

    /**
     * 특정 사용자가 좋아요 누른 게시글 조회 (페이징)
     * EXISTS 서브쿼리로 필터링
     */
    @Query("SELECT p FROM PostEntity p " +
           "WHERE EXISTS (" +
           "  SELECT ph FROM PostHeart ph " +
           "  WHERE ph.post = p AND ph.user = :user" +
           ")")
    Page<PostEntity> findPostsLikedByUser(
        @Param("user") UserEntity user,
        Pageable pageable
    );
 // ▼ 추가: Pageable 없이 리스트를 돌려주는 메서드
    List<PostEntity> findByUserOrderByCreatedAtDesc(UserEntity user);

    // ▼ 좋아요 누른 글도 동일
    @Query("""
       SELECT p FROM PostEntity p
       WHERE EXISTS (
           SELECT ph FROM PostHeart ph
           WHERE ph.post = p AND ph.user = :user
       )
    """)
    List<PostEntity> findPostsLikedByUser(@Param("user") UserEntity user);

}
