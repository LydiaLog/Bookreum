package com.bookreum.dev.domain.club;

import java.util.List;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bookreum.dev.domain.user.UserRepository;
import com.bookreum.dev.domain.user.UserEntity;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;  // 이미 존재한다고 가정

    /**
     * 새로운 채팅 메시지 전송
     * @param clubId 모임 ID
     * @param userId 보낸 사람 ID
     * @param content 메시지 내용
     * @return 저장된 ChatMessageEntity
     */
    @Transactional
    public ChatMessageEntity sendMessage(Long clubId, Long userId, String content) {
        ChatRoomEntity room = chatRoomRepository.findByClubId(clubId)
            .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다. 모임 ID=" + clubId));

        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자 ID: " + userId));

        ChatMessageEntity message = ChatMessageEntity.builder()
            .chatRoom(room)
            .user(user)
            .content(content)
            .sentAt(LocalDateTime.now())
            .build();

        return chatMessageRepository.save(message);
    }

    /**
     * 특정 채팅방의 모든 메시지 조회
     */
    @Transactional(readOnly = true)
    public List<ChatMessageEntity> getMessages(Long clubId) {
        ChatRoomEntity room = chatRoomRepository.findByClubId(clubId)
            .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다. 모임 ID=" + clubId));
        return chatMessageRepository.findByChatRoom(room);
    }
}
