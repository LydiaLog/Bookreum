package com.bookreum.dev.domain.club.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookreum.dev.domain.club.entity.ClubApplicationEntity;
import com.bookreum.dev.domain.club.entity.ClubEntity;
import com.bookreum.dev.domain.club.repository.ClubApplicationRepository;
import com.bookreum.dev.domain.user.UserEntity;

import lombok.RequiredArgsConstructor;

/**
* 북클럽 신청(가입) 로직을 처리하는 서비스
*/
@Service
@RequiredArgsConstructor
public class ClubApplicationService {

   private final ClubApplicationRepository clubApplicationRepository;

   /**
    * 사용자가 클럽에 신청(가입)합니다.
    * - 중복 신청 방지
    * - 최대 참가 인원 초과 방지
    * @param club 모임 엔티티
    * @param user 신청자 사용자 엔티티
    * @return 저장된 ClubApplicationEntity
    */
   @Transactional
   public ClubApplicationEntity applyToClub(ClubEntity club, UserEntity user) {
       validateDuplicateApplication(club, user);
       validateMaxParticipants(club);

       // 신청 정보 빌더 패턴으로 생성 후 저장
       return clubApplicationRepository.save(
           ClubApplicationEntity.builder()
               .club(club)
               .user(user)
               .build()
       );
   }

   /**
    * 이미 신청했는지 검사
    */
   private void validateDuplicateApplication(ClubEntity club, UserEntity user) {
       if (clubApplicationRepository.existsByClubAndUser(club, user)) {
           throw new IllegalStateException("이미 신청된 클럽입니다.");
       }
   }

   /**
    * 최대 참가 인원 초과 여부 검사
    */
   private void validateMaxParticipants(ClubEntity club) {
       long count = clubApplicationRepository.countByClub(club);
       Integer max = club.getMaxParticipants();
       if (max != null && count >= max) {
           throw new IllegalStateException(
               String.format("최대 참가 인원을 초과했습니다. (현재 %d / 최대 %d)", count, max)
           );
       }
   }

   /**
    * 사용자의 신청 취소
    */
   @Transactional
   public void cancelApplication(ClubEntity club, UserEntity user) {
       clubApplicationRepository.deleteByClubAndUser(club, user);
   }

   /**
    * 특정 클럽의 모든 신청 내역 조회 (읽기 전용)
    */
   @Transactional(readOnly = true)
   public List<ClubApplicationEntity> getApplications(ClubEntity club) {
       return clubApplicationRepository.findByClub(club);
   }

   /**
    * 사용자의 특정 클럽 신청 상태 확인
    */
   @Transactional(readOnly = true)
   public boolean isApplied(ClubEntity club, UserEntity user) {
       return clubApplicationRepository.existsByClubAndUser(club, user);
   }
}
