// ✅ ChatRoomService.java
package com.bookreum.dev.domain.club.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bookreum.dev.domain.club.entity.ChatRoomEntity;
import com.bookreum.dev.domain.club.entity.ClubEntity;
import com.bookreum.dev.domain.club.repository.ChatRoomRepository;
import com.bookreum.dev.domain.club.repository.ClubRepository;
import lombok.RequiredArgsConstructor;

/**
* 북클럽 채팅방 생성, 조회, 삭제 로직을 담당하는 서비스
*/
@Service
@RequiredArgsConstructor
public class ChatRoomService {

   private final ChatRoomRepository chatRoomRepository;
   private final ClubRepository clubRepository;

   /**
    * 클럽 ID로 채팅방 생성
    * @param clubId 모임 ID
    * @return 생성된 ChatRoomEntity
    */
   @Transactional
   public ChatRoomEntity createChatRoomForClub(Integer clubId) {
       // 모임 엔티티 로드
       ClubEntity club = clubRepository.findById(clubId)
           .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 모임 ID: " + clubId));

       // 이미 채팅방이 있으면 예외
       if (chatRoomRepository.existsByClubId(clubId)) {
           throw new IllegalStateException("이미 채팅방이 존재합니다. 모임 ID=" + clubId);
       }

       // 채팅방 엔티티 생성
       ChatRoomEntity room = ChatRoomEntity.builder()
           .club(club)
           .name(club.getTitle() + " 채팅방")
           .build();

       // 저장 후 반환
       return chatRoomRepository.save(room);
   }

   /**
    * 클럽 ID로 채팅방 조회
    */
   @Transactional(readOnly = true)
   public ChatRoomEntity getChatRoomByClub(Integer clubId) {
       return chatRoomRepository.findByClubId(clubId)
           .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다. 모임 ID=" + clubId));
   }

   /**
    * 채팅방 삭제 (ID 기반)
    */
   @Transactional
   public void deleteChatRoom(Integer chatRoomId) {
       ChatRoomEntity room = chatRoomRepository.findById(chatRoomId)
           .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다. 채팅방 ID=" + chatRoomId));
       chatRoomRepository.delete(room);
   }

   /**
    * 특정 모임의 채팅방 삭제 (클럽 ID 기반)
    */
   @Transactional
   public void deleteChatRoomByClub(Integer clubId) {
       ChatRoomEntity room = chatRoomRepository.findByClubId(clubId)
           .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다. 모임 ID=" + clubId));
       chatRoomRepository.delete(room);
   }
}
