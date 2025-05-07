package com.bookreum.dev.domain.club;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Long> {
	 /** 모임 ID로 채팅방 조회 */
    Optional<ChatRoomEntity> findByClubId(Long clubId);
    /** 모임 엔티티로도 조회 가능 */
    Optional<ChatRoomEntity> findByClub(ClubEntity club);
}
