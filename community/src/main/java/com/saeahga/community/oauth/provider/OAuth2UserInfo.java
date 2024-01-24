package com.saeahga.community.oauth.provider;

// 여러 가지 소셜 로그인 대응하기 위해서 Interface 로 선언
public interface OAuth2UserInfo {
    String getProviderId();
    String getProvider(); // 인증 공급자(ex. kakao, naver)
    String getName();
    String getEmail();
}
