// ✅ ChatRoomService.java
package com.bookreum.dev.domain.club.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bookreum.dev.domain.club.entity.ChatRoomEntity;
import com.bookreum.dev.domain.club.entity.ClubEntity;
import com.bookreum.dev.domain.club.repository.ChatRoomRepository;
import com.bookreum.dev.domain.club.repository.ClubRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    // ✅ 의존성 주입 (Repository)
    private final ChatRoomRepository chatRoomRepository;
    private final ClubRepository clubRepository;

    /**
     * ✅ 클럽(모임) ID로 채팅방 생성
     * 클럽이 존재하지 않으면 예외를 발생시키며, 이미 채팅방이 존재하는 경우도 예외 처리 가능.
     * @param clubId 모임 ID
     * @return 생성된 ChatRoomEntity (채팅방 엔티티)
     */
    @Transactional
    public ChatRoomEntity createChatRoomForClub(Long clubId) {
        // ✅ 1) 클럽(모임) 존재 여부 확인
        ClubEntity club = clubRepository.findById(clubId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 모임 ID: " + clubId));

        // ✅ 2) 동일 클럽에 이미 채팅방이 존재할 경우 예외 발생
        if (chatRoomRepository.existsByClubId(clubId)) {
            throw new IllegalStateException("이미 채팅방이 존재합니다. 모임 ID: " + clubId);
        }

        // ✅ 3) 새로운 채팅방 생성 (이름은 "모임명 + 채팅방")
        ChatRoomEntity room = ChatRoomEntity.builder()
            .club(club)
            .name(club.getTitle() + " 채팅방")  // 채팅방 이름은 "모임명 + 채팅방"
            .build();

        // ✅ 4) 생성된 채팅방 저장 및 반환
        return chatRoomRepository.save(room);
    }

    /**
     * ✅ 특정 모임의 채팅방 조회
     * @param clubId 모임 ID
     * @return ChatRoomEntity (채팅방 엔티티)
     * @throws IllegalArgumentException 존재하지 않는 클럽 또는 채팅방일 경우
     */
    @Transactional(readOnly = true)
    public ChatRoomEntity getChatRoomByClub(Long clubId) {
        // ✅ 클럽 ID로 채팅방 조회
        return chatRoomRepository.findByClubId(clubId)
            .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다. 모임 ID=" + clubId));
    }

    /**
     * ✅ 채팅방 삭제 (채팅방 ID로 삭제)
     * @param chatRoomId 삭제할 채팅방의 ID
     */
    @Transactional
    public void deleteChatRoom(Long chatRoomId) {
        // ✅ 1) 채팅방 조회
        ChatRoomEntity room = chatRoomRepository.findById(chatRoomId)
            .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다. 채팅방 ID=" + chatRoomId));

        // ✅ 2) 채팅방 삭제
        chatRoomRepository.delete(room);
    }

    /**
     * ✅ 특정 클럽의 채팅방 삭제 (클럽 ID로)
     * @param clubId 클럽 ID
     */
    @Transactional
    public void deleteChatRoomByClub(Long clubId) {
        // ✅ 1) 클럽 ID로 채팅방 조회
        ChatRoomEntity room = chatRoomRepository.findByClubId(clubId)
            .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다. 모임 ID=" + clubId));
        
        // ✅ 2) 채팅방 삭제
        chatRoomRepository.delete(room);
    }
}
