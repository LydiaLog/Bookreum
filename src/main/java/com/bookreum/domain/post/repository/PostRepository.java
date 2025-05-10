package com.bookreum.domain.post.repository;

import com.bookreum.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.bookreum.domain.user.entity.User;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

	//정렬
    List<Post> findAllByOrderByCreatedAtDesc(); // 최신순
    List<Post> findAllByOrderByCreatedAtAsc(); // 오래된순
    
    //검색
    @Query("SELECT p FROM Post p JOIN p.book b " +
    	       "WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :kw, '%')) " +
    	       "OR LOWER(b.title) LIKE LOWER(CONCAT('%', :kw, '%')) " +
    	       "OR LOWER(b.author) LIKE LOWER(CONCAT('%', :kw, '%')) " +
    	       "ORDER BY p.createdAt DESC")
    	List<Post> searchByKeywordOrderByLatest(@Param("kw") String keyword);

    	@Query("SELECT p FROM Post p JOIN p.book b " +
    	       "WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :kw, '%')) " +
    	       "OR LOWER(b.title) LIKE LOWER(CONCAT('%', :kw, '%')) " +
    	       "OR LOWER(b.author) LIKE LOWER(CONCAT('%', :kw, '%')) " +
    	       "ORDER BY p.createdAt ASC")
    	List<Post> searchByKeywordOrderByOldest(@Param("kw") String keyword);
    	
    	//조회
    	//내가 쓴 글 조회
    	List<Post> findByUserOrderByCreatedAtDesc(User user);
    	//내가 마음 누른 글 조회
    	List<Post> findByPostHearts_User(User user);
    	
    	//마이페이지
    	List<Post> findByUser(User user);

}