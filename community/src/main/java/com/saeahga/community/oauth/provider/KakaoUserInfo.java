package com.saeahga.community.oauth.provider;

import java.util.Map;

public class KakaoUserInfo implements OAuth2UserInfo {
    Map<String, Object> attributes;
    Map<String, Object> properties;

    public KakaoUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
        // kakao 는 "kakao_account" 에 유저정보가 있다.
        this.properties = (Map<String, Object>)attributes.get("kakao_account");
    }

    @Override
    public String getProviderId() {
        return attributes.get("id") + "";
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getName() {
        // "kakao_account" 안에 또 "profile"이라는 JSON 객체가 있다.
        Map profile = (Map)properties.get("profile");
        return profile.get("nickname") + "";
    }

    @Override
    public String getEmail() {
        return properties.get("email") + "";
    }
}
