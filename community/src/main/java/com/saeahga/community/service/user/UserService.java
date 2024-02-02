package com.saeahga.community.service.user;

import com.saeahga.community.entity.User;

import java.util.List;
import java.util.Map;

public interface UserService {
    // Repository 를 사용할 경우 매개변수는 Entity 로 지정해야 한다.

    // 테스트
    // 사용자 조회
    Map<String, Object> getUser(String userId);

    // 아이디 중복 체크
    User idCheck(User user);

    // 회원가입
    User join(User user);

    // 이름으로 사용자 수 조회
    int getUserNmCnt(User user);

    // 아이디 찾기(이름과 이메일로)
    List<User> findId(User user);
}
