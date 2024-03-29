package com.saeahga.community.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
// UserDetails 는, Spring Security 에서 사용자의 정보를 담는 인터페이스이다.
public class CustomUserDetails implements UserDetails, OAuth2User {
    private User user;

    // 소셜 로그인에서 사용자 정보를 담아줄 맵
    Map<String, Object> attributes;

    // 권한정보 제공
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> auths = new ArrayList<GrantedAuthority>();

        auths.add(
            new GrantedAuthority() {
                @Override
                public String getAuthority() {
                    return user.getUserType();
                }
            }
        );

        return auths;
    }

    // 비밀번호 제공
    @Override
    public String getPassword() {
        return user.getUserPw();
    }

    // 아이디 제공
    @Override
    public String getUsername() {
        return user.getUserId();
    }

    // 계정 만료 여부
    // 사용 안할 때는 항상 true 반환되도록 처리
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정 잠김 여부
    // 사용 안할 때는 항상 true 반환되도록 처리
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 계정 인증정보를 항상 저장할 지 여부
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 계정 활성화 여부
    @Override
    public boolean isEnabled() {
        return true;
    }

    // OAuth2AuthenticatedPrincipal 인터페이스 메소드
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    // AuthenticatedPrincipal 인터페이스 메소드
    @Override
    public String getName() {
        return user.getUserNm();
    }
}
