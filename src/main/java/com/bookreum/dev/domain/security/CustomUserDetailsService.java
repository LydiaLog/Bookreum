package com.bookreum.dev.domain.security;

import com.bookreum.dev.domain.user.UserEntity;
import com.bookreum.dev.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service  // Spring 빈으로 등록
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String kakaoId) throws UsernameNotFoundException {
        // Remember-Me 에 의해 호출되며 kakaoId로 사용자 조회
        UserEntity user = userRepository.findByKakaoId(kakaoId)
            .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + kakaoId));

        // Spring Security UserDetails 구현체 반환
        return User.builder()
            .username(user.getKakaoId())  // 식별자
            .password("")  // Remember-Me에서는 실제 비밀번호 필요 없음
            .disabled(user.isDel())  // 탈퇴/비활성화 시 로그인 차단
            .authorities(Collections.emptyList())  // 권한은 빈 리스트
            .build();
    }
}