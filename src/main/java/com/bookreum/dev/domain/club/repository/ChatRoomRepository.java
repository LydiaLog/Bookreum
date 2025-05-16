package com.bookreum.dev.domain.club.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.bookreum.dev.domain.club.entity.ChatRoomEntity;
import com.bookreum.dev.domain.club.entity.ClubEntity;
import com.bookreum.dev.domain.user.UserEntity;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Integer> {

    /**
     * 클럽 ID를 이용해 채팅방을 조회합니다.
     *
     * @param clubId 모임 ID
     * @return 채팅방 엔티티(Optional)
     */
    Optional<ChatRoomEntity> findByClubId(Integer clubId);

    /**
     * 클럽 엔티티를 이용해 채팅방을 조회합니다.
     *
     * @param club 모임 엔티티
     * @return 채팅방 엔티티(Optional)
     */
    Optional<ChatRoomEntity> findByClub(ClubEntity club);

    /**
     * 해당 클럽에 채팅방이 존재하는지 여부를 반환합니다.
     *
     * @param clubId 모임 ID
     * @return 존재하면 true, 아니면 false
     */
    boolean existsByClubId(Integer clubId);

    /**
     * 클럽 엔티티로 채팅방 존재 여부를 확인합니다.
     *
     * @param club 모임 엔티티
     * @return 존재하면 true, 아니면 false
     */
    boolean existsByClub(ClubEntity club);
    


    /** 사용자가 참여 중인 모든 채팅방 조회 */
    @Query("""
       SELECT cr FROM ChatRoomEntity cr
       JOIN cr.club c
       JOIN ClubApplicationEntity a ON a.club = c
       WHERE a.user = :user
    """)
    List<ChatRoomEntity> findRoomsByUser(@Param("user") UserEntity user);


}

