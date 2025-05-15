package com.bookreum.dev.domain.security;

import com.bookreum.dev.domain.user.UserEntity;
import com.bookreum.dev.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

/**
 * 🔐 카카오 OAuth2 로그인 시 사용자 정보를 조회·동기화하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;   // DB 사용자 정보 접근

    /**
     * 1️⃣ Kakao API 로부터 사용자 프로필을 가져온 뒤  
     * 2️⃣ 우리 DB(UserEntity)에 저장(신규)·업데이트(기존) 하고  
     * 3️⃣ Spring Security 에서 사용할 OAuth2User 를 반환한다.
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest)
            throws OAuth2AuthenticationException {

        /* 1) Kakao에서 사용자 프로필 조회 */
        OAuth2User kakaoUser   = super.loadUser(userRequest);
        Map<String, Object> attributes = kakaoUser.getAttributes();

        /* 2) 프로필 속성 파싱 */
        String kakaoId = attributes.get("id").toString();

        @SuppressWarnings("unchecked")
        Map<String, Object> props = (Map<String, Object>) attributes.get("properties");
        String nickname     = (String) props.get("nickname");
        String profileImage = (String) props.get("profile_image");

        log.debug("[OAUTH2] Kakao login ▶ id={}, nickname={}", kakaoId, nickname);

        /* 3) DB 동기화 (신규 저장 or 기존 업데이트) */
        UserEntity user = userRepository.findByKakaoId(kakaoId)
            .map(u -> {                                     // 기존 회원이면
                // 닉네임·프로필이 변경됐을 수 있으니 업데이트
                u.updateProfile(nickname, profileImage);
                return userRepository.save(u);
            })
            .orElseGet(() -> {                              // 신규 회원이면
                UserEntity created = userRepository.save(
                    UserEntity.builder()
                              .kakaoId(kakaoId)
                              .nickname(nickname)
                              .profileImage(profileImage)
                              .build()
                );
                log.info("[USER] New Kakao user saved ▶ dbId={}, kakaoId={}",
                         created.getId(), created.getKakaoId());
                return created;
            });

        /* 4) Spring Security 쪽으로 반환할 OAuth2User 생성
              - 권한은 ROLE_USER 하나만 부여 */
        return new DefaultOAuth2User(
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
            attributes,
            "id" // OAuth2User의 nameAttributeKey
        );
    }
}
