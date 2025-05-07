// com/bookreum/dev/domain/club/ChatRoomService.java
package com.bookreum.dev.domain.club;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final ClubRepository clubRepository;

    /**
     * 모임 생성 시, 해당 모임에 딸린 채팅방을 만듭니다.
     * @param clubId 모임 ID
     * @return 생성된 ChatRoomEntity
     */
    @Transactional
    public ChatRoomEntity createChatRoomForClub(Long clubId) {
        ClubEntity club = clubRepository.findById(clubId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 모임 ID: " + clubId));

        ChatRoomEntity room = ChatRoomEntity.builder()
            .club(club)
            .name(club.getTitle() + " 채팅방")  // builder 가 name() 메서드 포함!
            .build();

        return chatRoomRepository.save(room);
    }

    /**
     * 특정 모임의 채팅방 조회
     */
    @Transactional(readOnly = true)
    public ChatRoomEntity getChatRoomByClub(Long clubId) {
        return chatRoomRepository.findByClubId(clubId)
            .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다. 모임 ID=" + clubId));
    }
}
