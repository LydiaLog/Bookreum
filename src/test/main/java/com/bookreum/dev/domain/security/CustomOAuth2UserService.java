package com.bookreum.dev.domain.security;

import com.bookreum.dev.domain.user.UserEntity;
import com.bookreum.dev.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // Super 호출로 Kakao API 에서 유저 정보 받아오기
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String,Object> attributes = oAuth2User.getAttributes();

        // Kakao 고유 ID, 니컬네임, 프로필 이미지 추출
        String kakaoId = attributes.get("id").toString();
        @SuppressWarnings("unchecked")
        Map<String,Object> props = (Map<String,Object>) attributes.get("properties");
        String nickname    = (String) props.get("nickname");
        String profileImage= (String) props.get("profile_image");

        // DB에 없으면 신규 저장, 있으면 기존 엔티티 로드
        UserEntity user = userRepository.findByKakaoId(kakaoId)
            .orElseGet(() -> userRepository.save(
                UserEntity.builder()
                          .kakaoId(kakaoId)
                          .nickname(nickname)
                          .profileImage(profileImage)
                          .social(true)
                          .del(false)
                          .build()
            ));

        // 인증된 OAuth2User 생성 (권한은 빈 리스트로 처리)
        return new DefaultOAuth2User(
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),  // 필요 시 ROLE_USER 부여
            attributes,
            "id"
        );
    }
}
