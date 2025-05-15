package com.bookreum.dev.domain.club.repository;

import com.bookreum.dev.domain.club.entity.ChatMessageEntity;
import com.bookreum.dev.domain.club.entity.ChatRoomEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {

    // ✅ 채팅방의 메시지를 페이징하여 조회
    Page<ChatMessageEntity> findByChatRoom(ChatRoomEntity chatRoom, Pageable pageable);

    // ✅ 채팅방의 전체 메시지 수 조회
    long countByChatRoom(ChatRoomEntity chatRoom);

    // ✅ 채팅방의 모든 메시지 삭제
    void deleteAllByChatRoom(ChatRoomEntity chatRoom);
}
