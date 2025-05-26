//package com.bookreum.dev.domain.mypage;
//
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import lombok.RequiredArgsConstructor;
//
//@RestController
//@RequestMapping("/api/mypage")
//@RequiredArgsConstructor
//public class MyPageController {
//    private final MyPageService myPageService;
//
//    @GetMapping("/{userId}")
//    public ResponseEntity<MyPageDTO> myPage(@PathVariable Integer userId) {
//        return ResponseEntity.ok(myPageService.getMyPage(userId));
//    }
//}
//
