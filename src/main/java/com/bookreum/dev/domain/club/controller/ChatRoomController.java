package com.bookreum.dev.domain.club.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
     * 해당 모임에 딸린 채팅방을 생성합니다.
     * 이미 생성되어 있다면 예외가 발생하거나 중복 생성이 될 수 있으니, 
     * 호출 전에 로직을 조정하세요.
     * 
     * POST /api/clubs/{clubId}/chatroom
     */
    @PostMapping
    public ResponseEntity<ChatRoomEntity> createChatRoom(@PathVariable Long clubId) {
        ChatRoomEntity room = chatRoomService.createChatRoomForClub(clubId);
        return ResponseEntity.status(HttpStatus.CREATED).body(room);
    }

    /**
     * 특정 모임의 채팅방 정보를 조회합니다.
     * 
     * GET /api/clubs/{clubId}/chatroom
     */
    @GetMapping
    public ResponseEntity<ChatRoomEntity> getChatRoom(@PathVariable Long clubId) {
        ChatRoomEntity room = chatRoomService.getChatRoomByClub(clubId);
        return ResponseEntity.ok(room);
    }
}


