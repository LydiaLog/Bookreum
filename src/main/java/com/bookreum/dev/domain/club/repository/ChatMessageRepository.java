package com.bookreum.dev.domain.club.repository;

import com.bookreum.dev.domain.club.entity.ChatMessageEntity;
import com.bookreum.dev.domain.club.entity.ChatRoomEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Integer> {

    /**
     * 주어진 채팅방의 메시지를 페이지 단위로 조회합니다.
     *
     * @param chatRoom 채팅방 엔티티
     * @param pageable 페이지 및 정렬 정보
     * @return 메시지 페이지
     */
    Page<ChatMessageEntity> findByChatRoom(ChatRoomEntity chatRoom, Pageable pageable);

    /**
     * 주어진 채팅방의 전체 메시지 수를 반환합니다.
     *
     * @param chatRoom 채팅방 엔티티
     * @return 메시지 총 개수
     */
    long countByChatRoom(ChatRoomEntity chatRoom);

    /**
     * 주어진 채팅방의 모든 메시지를 삭제합니다.
     *
     * @param chatRoom 채팅방 엔티티
     */
    void deleteAllByChatRoom(ChatRoomEntity chatRoom);
}

