package com.saeahga.community.service.user.impl;

import com.saeahga.community.mapper.UserMapper;
import com.saeahga.community.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;

    // @RequiredArgsConstructor 어노테이션을 사용하여 생성자 주입 시, 아래와 같이 생성자 생성 부분을 대신해준다.
//    @Autowired
//    public UserServiceImpl(UserMapper userMapper) {
//        this.userMapper = userMapper;
//    }

    @Override
    public Map<String, Object> getUser(String userId) {
        return userMapper.getUser(userId);
    }

}
