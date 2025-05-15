package com.bookreum.dev.domain.club.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bookreum.dev.domain.club.dto.ChatRoomDTO;
import com.bookreum.dev.domain.club.entity.ChatRoomEntity;
import com.bookreum.dev.domain.club.service.ChatRoomService;

import lombok.RequiredArgsConstructor;

/**
 * 모임별 채팅방 생성/조회 REST API
 */
@RestController
@RequestMapping("/api/clubs/{clubId}/chatroom")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    /**
     * 모임에 딸린 채팅방 생성
     * @param clubId 모임 ID
     * @return 생성된 채팅방 DTO와 HTTP 201
     */
    @PostMapping
    public ResponseEntity<ChatRoomDTO> createChatRoom(@PathVariable Integer clubId) {
        ChatRoomEntity room = chatRoomService.createChatRoomForClub(clubId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ChatRoomDTO.fromEntity(room));
    }

    /**
     * 특정 모임의 채팅방 조회
     * @param clubId 모임 ID
     * @return 채팅방 DTO
     */
    @GetMapping
    public ResponseEntity<ChatRoomDTO> getChatRoom(@PathVariable Integer clubId) {
        ChatRoomEntity room = chatRoomService.getChatRoomByClub(clubId);
        return ResponseEntity.ok(ChatRoomDTO.fromEntity(room));
    }
}


