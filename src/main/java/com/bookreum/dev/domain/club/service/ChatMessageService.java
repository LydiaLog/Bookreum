package com.bookreum.dev.domain.club.service;

import java.util.List;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bookreum.dev.domain.user.UserRepository;
import com.bookreum.dev.domain.club.entity.ChatMessageEntity;
import com.bookreum.dev.domain.club.entity.ChatRoomEntity;
import com.bookreum.dev.domain.club.repository.ChatMessageRepository;
import com.bookreum.dev.domain.club.repository.ChatRoomRepository;
import com.bookreum.dev.domain.user.UserEntity;
import lombok.RequiredArgsConstructor;

/**
 * 채팅 메시지 발송 및 조회, 삭제 로직을 수행하는 서비스
 */
@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    /**
     * 새로운 메시지를 전송 및 저장합니다.
     * @param clubId 모임 ID
     * @param userId 전송자 사용자 ID
     * @param content 메시지 내용
     * @return 저장된 ChatMessageEntity
     */
    @Transactional
    public ChatMessageEntity sendMessage(Integer clubId, Integer userId, String content) {
        // 메시지 내용 검사
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("메시지 내용은 비어있을 수 없습니다.");
        }
        
        // 채팅방 조회
        ChatRoomEntity room = chatRoomRepository.findByClubId(clubId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 모임 ID: " + clubId));

        // 사용자 조회
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자 ID: " + userId));

        // 메시지 엔티티 생성
        ChatMessageEntity message = ChatMessageEntity.builder()
            .chatRoom(room)
            .user(user)
            .content(content)
            .sentAt(LocalDateTime.now())
            .build();

        // 저장 후 반환
        return chatMessageRepository.save(message);
    }

    /**
     * 특정 채팅방의 메시지 목록을 페이징 조회합니다.
     * @param clubId 모임 ID
     * @param page 페이지 번호 (0-based)
     * @param size 페이지 크기
     * @return 메시지 엔티티 리스트
     */
    @Transactional(readOnly = true)
    public List<ChatMessageEntity> getMessages(Integer clubId, int page, int size) {
        ChatRoomEntity room = chatRoomRepository.findByClubId(clubId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 모임 ID: " + clubId));
        Pageable pageable = PageRequest.of(page, size);
        Page<ChatMessageEntity> msgPage = chatMessageRepository.findByChatRoom(room, pageable);
        return msgPage.getContent();
    }

    /**
     * 특정 채팅방의 메시지 총 개수를 반환합니다.
     * @param clubId 모임 ID
     * @return 메시지 개수
     */
    @Transactional(readOnly = true)
    public long getMessageCount(Integer clubId) {
        ChatRoomEntity room = chatRoomRepository.findByClubId(clubId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 모임 ID: " + clubId));
        return chatMessageRepository.countByChatRoom(room);
    }

    /**
     * 단일 메시지 조회 (ID 기반)
     */
    @Transactional(readOnly = true)
    public ChatMessageEntity getMessageById(Integer messageId) {
        return chatMessageRepository.findById(messageId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메시지 ID: " + messageId));
    }

    /**
     * 특정 메시지를 삭제합니다.
     */
    @Transactional
    public void deleteMessage(Integer messageId) {
        if (!chatMessageRepository.existsById(messageId)) {
            throw new IllegalArgumentException("존재하지 않는 메시지 ID: " + messageId);
        }
        chatMessageRepository.deleteById(messageId);
    }

    /**
     * 채팅방 내 모든 메시지를 삭제합니다.
     */
    @Transactional
    public void deleteAllMessagesInRoom(Integer clubId) {
        ChatRoomEntity room = chatRoomRepository.findByClubId(clubId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 모임 ID: " + clubId));
        chatMessageRepository.deleteAllByChatRoom(room);
    }
}

