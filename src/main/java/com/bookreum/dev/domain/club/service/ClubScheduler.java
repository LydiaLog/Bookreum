//package com.bookreum.dev.domain.club.service;
//
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import lombok.RequiredArgsConstructor;
//
//@Component
//@RequiredArgsConstructor
//public class ClubScheduler {
//    
//    private final ClubService clubService;
//    
//    /**
//     * 매 시간마다 마감된 클럽의 상태를 업데이트합니다.
//     */
//    @Scheduled(cron = "0 0 * * * *")
//    public void updateClosedClubs() {
//        clubService.updateClosedClubs();
//    }
//}
