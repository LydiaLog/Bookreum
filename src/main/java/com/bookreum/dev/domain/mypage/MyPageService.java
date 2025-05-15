package com.bookreum.dev.domain.mypage;

import org.springframework.stereotype.Service;

import com.bookreum.dev.domain.club.dto.ChatRoomDTO;
import com.bookreum.dev.domain.club.dto.ClubDTO;
import com.bookreum.dev.domain.club.repository.ChatRoomRepository;
import com.bookreum.dev.domain.club.repository.ClubApplicationRepository;
import com.bookreum.dev.domain.post.dto.PostDto;
import com.bookreum.dev.domain.post.repository.PostHeartRepository;
import com.bookreum.dev.domain.post.repository.PostRepository;
import com.bookreum.dev.domain.user.UserDTO;
import com.bookreum.dev.domain.user.UserEntity;
import com.bookreum.dev.domain.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final UserRepository userRepository;          // ⬅️ 추가
    private final PostRepository postRepo;
    private final PostHeartRepository heartRepo;
    private final ClubApplicationRepository appRepo;
    private final ChatRoomRepository roomRepo;

    public MyPageDTO getMyPage(Integer userId) {
        // ① 사용자 조회 (없으면 예외)
        UserEntity me = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자 ID: " + userId));

        // ② 나의 글
        var myPosts = postRepo.findByUserOrderByCreatedAtDesc(me).stream()
                .map(PostDto.Response::fromEntity)
                .toList();

        // ③ 좋아요 누른 글
        var liked = heartRepo.findPostsLikedByUser(me).stream()
                .map(PostDto.Response::fromEntity)
                .toList();

        // ④ 신청한 모임
        var apps = appRepo.findByUser(me).stream()
                .map(app -> ClubDTO.fromEntity(app.getClub()))
                .toList();

        // ⑤ 참여 중 채팅방
        var rooms = roomRepo.findRoomsByUser(me).stream()
                .map(ChatRoomDTO::fromEntity)
                .toList();

        return MyPageDTO.builder()
                .profile(UserDTO.fromEntity(me))
                .myPosts(myPosts)
                .likedPosts(liked)
                .appliedClubs(apps)
                .chatRooms(rooms)
                .build();
    }
}

