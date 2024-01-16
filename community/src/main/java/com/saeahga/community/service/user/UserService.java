package com.saeahga.community.service.user;

import com.saeahga.community.entity.User;

import java.util.Map;

public interface UserService {
    // Repository 를 사용할 경우 매개변수는 Entity 로 지정해야 한다.

    // 테스트
    // 사용자 조회
    public Map<String, Object> getUser(String userId);

    // 아이디 중복 체크
    public User idCheck(User user);
}
