//package com.bookreum.dev.domain.mypage;
//
//import org.springframework.stereotype.Service;
//
//import com.bookreum.dev.domain.club.dto.ChatRoomDTO;
//import com.bookreum.dev.domain.club.dto.ClubDTO;
//import com.bookreum.dev.domain.club.repository.ChatRoomRepository;
//import com.bookreum.dev.domain.club.repository.ClubApplicationRepository;
//import com.bookreum.dev.domain.post.dto.PostDto;
//import com.bookreum.dev.domain.post.repository.PostHeartRepository;
//import com.bookreum.dev.domain.post.repository.PostRepository;
//import com.bookreum.dev.domain.user.UserDTO;
//import com.bookreum.dev.domain.user.UserEntity;
//import com.bookreum.dev.domain.user.UserRepository;
//
//import lombok.RequiredArgsConstructor;
//
//@Service
//@RequiredArgsConstructor
//public class MyPageService {
//
//    private final UserRepository userRepository;          // ⬅️ 추가
//    private final PostRepository postRepo;
//    private final PostHeartRepository heartRepo;
//    private final ClubApplicationRepository appRepo;
//    private final ChatRoomRepository roomRepo;
//
//    public MyPageDTO getMyPage(Integer userId) {
//        // ① 사용자 조회 (없으면 예외)
//        UserEntity me = userRepository.findById(userId)
//            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자 ID: " + userId));
//
//        // ① 내가 쓴 글: 실제 좋아요·댓글 수 노출
//        var myPosts = postRepo.findByUserOrderByCreatedAtDesc(me).stream()
//            .map(post -> {
//                long heartCount   = heartRepo.countByPostId(post.getId());
//                long commentCount = 0L; // 댓글 기능 미구현 시 기본 0
//                return PostDto.Response.fromEntity(post, me, heartCount, commentCount);
//            })
//            .toList();
//
//        // ② 내가 좋아요 누른 글(남의 글): 좋아요·댓글 수 숨김
//        var likedPosts = postRepo.findPostsLikedByUser(me).stream()
//            .map(post ->
//                // heartCount, commentCount 모두 0L로 고정
//                PostDto.Response.fromEntity(post, me, 0L, 0L)
//            )
//            .toList();
//
//        // ④ 신청한 모임
//        var apps = appRepo.findByUser(me).stream()
//                .map(app -> ClubDTO.fromEntity(app.getClub()))
//                .toList();
//
//        // ⑤ 참여 중 채팅방
//        var rooms = roomRepo.findRoomsByUser(me).stream()
//                .map(ChatRoomDTO::fromEntity)
//                .toList();
//
//        return MyPageDTO.builder()
//                .profile(UserDTO.fromEntity(me))
//                .myPosts(myPosts)
//                .likedPosts(likedPosts)
//                .appliedClubs(apps)
//                .chatRooms(rooms)
//                .build();
//    }
//}
//
