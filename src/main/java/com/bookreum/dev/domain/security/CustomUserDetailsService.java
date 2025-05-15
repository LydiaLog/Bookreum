package com.bookreum.dev.domain.security;

import com.bookreum.dev.domain.user.UserEntity;
import com.bookreum.dev.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * 🔐 Remember-Me 토큰이 있을 때 카카오 ID(=username)로
 *     DB 사용자 정보를 로드해 Authentication 을 복원한다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Remember-Me 쿠키가 보낸 kakaoId 로 사용자 조회
     * @param kakaoId 카카오 고유 ID (subject)
     */
    @Override
    public UserDetails loadUserByUsername(String kakaoId) throws UsernameNotFoundException {

        // 1) DB 조회
        UserEntity user = userRepository.findByKakaoId(kakaoId)
            .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + kakaoId));

        log.debug("[REMEMBER-ME] 사용자 복원 ▶ kakaoId={}, nickname={}", kakaoId, user.getNickname());

        // 2) UserDetails 생성 (패스워드/권한은 사용하지 않음)
        return User.builder()
                   .username(user.getKakaoId())
                   .authorities(Collections.emptyList())
                   .build();
    }
}
