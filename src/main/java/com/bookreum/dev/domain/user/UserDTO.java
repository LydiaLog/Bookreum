package com.bookreum.dev.domain.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
	private Integer id;             // DB 식별자
    private String kakaoId;         // 카카오 ID
    private String nickname;        // 닉네임
    private String profileImage;    // 프로필 이미지 URL
}

