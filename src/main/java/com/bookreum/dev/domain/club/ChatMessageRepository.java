package com.bookreum.dev.domain.club;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {
	/**
     * 채팅방에 속한 모든 메시지 조회 (발송 시각 순)
     */
	List<ChatMessageEntity> findByChatRoom(ChatRoomEntity chatRoom);
}
