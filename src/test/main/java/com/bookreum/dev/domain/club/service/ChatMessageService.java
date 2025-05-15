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

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    /**
     * 새로운 채팅 메시지 전송
     * @param clubId 모임 ID
     * @param userId 보낸 사람 ID
     * @param content 메시지 내용
     * @return 저장된 ChatMessageEntity
     */
    @Transactional
    public ChatMessageEntity sendMessage(Long clubId, Long userId, String content) {
        // 메시지 내용 유효성 검사
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("메시지 내용은 비어있을 수 없습니다.");
        }

        // 클럽(모임) 확인
        ChatRoomEntity room = chatRoomRepository.findByClubId(clubId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 모임 ID: " + clubId));

        // 사용자 확인
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자 ID: " + userId));

        // 메시지 생성 및 저장
        ChatMessageEntity message = ChatMessageEntity.builder()
            .chatRoom(room)
            .user(user)
            .content(content)
            .sentAt(LocalDateTime.now())
            .build();

        return chatMessageRepository.save(message);
    }

    /**
     * 특정 채팅방의 모든 메시지 조회 (페이징)
     * @param clubId 모임 ID
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 메시지 목록
     */
    @Transactional(readOnly = true)
    public List<ChatMessageEntity> getMessages(Long clubId, int page, int size) {
        ChatRoomEntity room = chatRoomRepository.findByClubId(clubId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 모임 ID: " + clubId));

        Pageable pageable = PageRequest.of(page, size);
        Page<ChatMessageEntity> messagePage = chatMessageRepository.findByChatRoom(room, pageable);
        return messagePage.getContent();
    }

    /**
     * 특정 채팅방의 전체 메시지 수 조회
     * @param clubId 모임 ID
     * @return 메시지 수
     */
    @Transactional(readOnly = true)
    public long getMessageCount(Long clubId) {
        ChatRoomEntity room = chatRoomRepository.findByClubId(clubId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 모임 ID: " + clubId));
        return chatMessageRepository.countByChatRoom(room);
    }

    /**
     * 특정 메시지 조회 (ID로)
     * @param messageId 메시지 ID
     * @return 메시지 엔티티
     */
    @Transactional(readOnly = true)
    public ChatMessageEntity getMessageById(Long messageId) {
        return chatMessageRepository.findById(messageId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메시지 ID: " + messageId));
    }

    /**
     * 메시지 삭제 (ID로)
     * @param messageId 메시지 ID
     */
    @Transactional
    public void deleteMessage(Long messageId) {
        if (!chatMessageRepository.existsById(messageId)) {
            throw new IllegalArgumentException("존재하지 않는 메시지 ID: " + messageId);
        }
        chatMessageRepository.deleteById(messageId);
    }

    /**
     * 특정 채팅방의 모든 메시지 삭제
     * @param clubId 모임 ID
     */
    @Transactional
    public void deleteAllMessagesInRoom(Long clubId) {
        ChatRoomEntity room = chatRoomRepository.findByClubId(clubId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 모임 ID: " + clubId));
        chatMessageRepository.deleteAllByChatRoom(room);
    }
}
