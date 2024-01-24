package com.saeahga.community.service.user.impl;

import com.saeahga.community.entity.CustomUserDetails;
import com.saeahga.community.entity.User;
import com.saeahga.community.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
// @RequiredArgsConstructor 는 초기화 되지않은 final 필드나, @NonNull 이 붙은 필드에 대해 생성자를 생성해준다.
@RequiredArgsConstructor
// UserDetailsService 는, Spring Security 에서 DB 로부터 유저의 정보를 가져오는 인터페이스이다.
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    // loadUserByUsername 메서드로 로그인한 유저가 DB에 저장되어 있는지를 찾는다.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(userRepository.findById(username).isEmpty()) {
            return null;
        } else {
            User user = userRepository.findById(username).get();

            return CustomUserDetails.builder()
                    .user(user)
                    .build();
        }
    }
}
