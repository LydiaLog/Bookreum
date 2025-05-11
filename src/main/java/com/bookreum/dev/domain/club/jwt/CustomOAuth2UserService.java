package com.bookreum.dev.domain.club.jwt;

import java.util.Map;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

/**
 * OAuth2 로그인(카카오) 후 전달받은 사용자 정보를 가공·저장하고
 * Spring Security 에 인증된 Principal(OAuth2User)로 넘겨주는 서비스
 */
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oauth2User = super.loadUser(userRequest);

        // 1) 카카오에서 넘어온 전체 속성맵 꺼내기
        Map<String,Object> attributes = oauth2User.getAttributes();

        // 2) id는 최상위, email 같은 세부 필드는 nested map 에 있음
        String kakaoId = attributes.get("id").toString();
        @SuppressWarnings("unchecked")
        Map<String,Object> kakaoAccount = (Map<String,Object>) attributes.get("kakao_account");
   

        // 필요하다면 DB 저장 로직 추가…

        return oauth2User;
    }
}

