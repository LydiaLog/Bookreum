package com.bookreum.dev.domain.club.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.bookreum.dev.domain.club.entity.ChatRoomEntity;
import com.bookreum.dev.domain.club.entity.ClubEntity;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Long> {

    /**
     * ✅ 모임 ID로 채팅방 조회
     * - 특정 클럽 (모임) ID에 연결된 채팅방을 조회합니다.
     * - 클럽(모임)이 존재하지만 채팅방이 없는 경우 Optional.empty() 반환.
     * 
     * @param clubId 클럽(모임) ID
     * @return Optional<ChatRoomEntity> 클럽에 연결된 채팅방 (존재하지 않으면 비어 있음)
     */
    Optional<ChatRoomEntity> findByClubId(Long clubId);

    /**
     * ✅ 모임 엔티티로 채팅방 조회
     * - 클럽(모임) 엔티티 자체로도 채팅방을 조회할 수 있습니다.
     * - 클럽 엔티티를 직접 사용하여 더 직관적인 조회 가능.
     * 
     * @param club 클럽(모임) 엔티티
     * @return Optional<ChatRoomEntity> 클럽에 연결된 채팅방 (존재하지 않으면 비어 있음)
     */
    Optional<ChatRoomEntity> findByClub(ClubEntity club);

    /**
     * ✅ 클럽 ID로 채팅방 존재 여부 확인
     * - 특정 클럽(모임)에 채팅방이 존재하는지 확인할 수 있습니다.
     * - 중복 채팅방 생성 방지를 위한 유용한 메서드.
     * 
     * @param clubId 클럽(모임) ID
     * @return boolean 해당 클럽에 채팅방이 존재하면 true
     */
    boolean existsByClubId(Long clubId);

    /**
     * ✅ 클럽 엔티티로 채팅방 존재 여부 확인
     * - 클럽 엔티티 자체로도 존재 여부 확인 가능.
     * 
     * @param club 클럽(모임) 엔티티
     * @return boolean 해당 클럽에 채팅방이 존재하면 true
     */
    boolean existsByClub(ClubEntity club);
}
