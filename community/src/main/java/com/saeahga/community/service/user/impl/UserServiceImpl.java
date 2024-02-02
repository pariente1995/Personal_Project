package com.saeahga.community.service.user.impl;

import com.saeahga.community.entity.User;
import com.saeahga.community.mapper.UserMapper;
import com.saeahga.community.repository.UserRepository;
import com.saeahga.community.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
// @RequiredArgsConstructor 는 초기화 되지않은 final 필드나, @NonNull 이 붙은 필드에 대해 생성자를 생성해준다.
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    // @RequiredArgsConstructor 어노테이션을 사용하여 생성자 주입 시, 아래와 같이 생성자 생성 부분을 대신해준다.
//    @Autowired
//    public UserServiceImpl(UserMapper userMapper) {
//        this.userMapper = userMapper;
//    }

    @Override
    public Map<String, Object> getUser(String userId) {
        return userMapper.getUser(userId);
    }

    // 아이디 중복 체크
    @Override
    public User idCheck(User user) {
        if(!userRepository.findById(user.getUserId()).isEmpty()) {
            return userRepository.findById(user.getUserId()).get();
        } else {
            return null;
        }
    }

    // 회원가입
    @Override
    public User join(User user) {
        return userRepository.save(user);
    }

    // 이름으로 사용자 수 조회
    @Override
    public int getUserNmCnt(User user) {
        return userRepository.getUserNmCnt(user.getUserNm());
    }

    // 아이디 찾기(이름과 이메일로)
    @Override
    public List<User> findId(User user) {
        return userRepository.findByUserNmAndUserEmail(user.getUserNm(), user.getUserEmail());
    }
}
