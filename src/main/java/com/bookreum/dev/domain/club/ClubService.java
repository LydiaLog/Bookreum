// com/bookreum/dev/domain/club/ClubService.java
package com.bookreum.dev.domain.club;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClubService {
    private final ClubRepository clubRepository;

    /**
     * 새 모임 생성
     * @param club 모임 엔티티
     * @return 저장된 모임
     */
    @Transactional
    public ClubEntity createClub(ClubEntity club) {
        return clubRepository.save(club);
    }

    /**
     * 특정 모임 조회
     * @param id 모임 ID
     */
    @Transactional(readOnly = true)
    public ClubEntity getClub(Long id) {
        return clubRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("모임을 찾을 수 없습니다. ID=" + id));
    }

    /**
     * 모든 모임 목록 조회
     */
    @Transactional(readOnly = true)
    public List<ClubEntity> listClubs() {
        return clubRepository.findAll();
    }

    /**
     * 모임 삭제
     */
    @Transactional
    public void deleteClub(Long id) {
        clubRepository.deleteById(id);
    }
}
