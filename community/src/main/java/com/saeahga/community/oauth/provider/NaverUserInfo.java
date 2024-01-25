package com.saeahga.community.oauth.provider;

import java.util.Map;

public class NaverUserInfo implements OAuth2UserInfo {
    Map<String, Object> attributes;

    public NaverUserInfo(Map<String, Object> attributes) {
        this.attributes = (Map<String, Object>)attributes.get("response");
    }

    @Override
    public String getProviderId() {
        /*
            동일인 식별 정보.
            동일인 식별 정보는 네이버 아이디마다 고유하게 발급되는 값이다.
        */
        return attributes.get("id") + "";
    }

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getName() {
        return attributes.get("name") + "";
    }

    @Override
    public String getEmail() {
        return attributes.get("email") + "";
    }
}
