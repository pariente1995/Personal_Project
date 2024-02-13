package com.saeahga.community.oauth;

import com.saeahga.community.entity.CustomUserDetails;
import com.saeahga.community.entity.User;
import com.saeahga.community.oauth.provider.KakaoUserInfo;
import com.saeahga.community.oauth.provider.NaverUserInfo;
import com.saeahga.community.oauth.provider.OAuth2UserInfo;
import com.saeahga.community.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class Oauth2UserService extends DefaultOAuth2UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    // 토큰 발행 후 사용자 정보에 대한 처리
    // 소셜 로그인 버튼 클릭 -> 사용자 동의 창 -> 사용자 동의 후 로그인 완료 -> code 값 리턴 ->
    // 토큰수령 -> 토큰을 통해 사용자 정보 취득 -> loadUser 메소드 자동 호출 -> 사용자 정보를 커스터마이징
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> temp = oAuth2User.getAttributes();

//        Iterator<String> iter = temp.keySet().iterator();
//
//        while(iter.hasNext()) {
//            System.out.println(iter.next());
//            System.out.println(userRequest.getAccessToken().getTokenValue());
//        }

        String userName= "";
        String providerId = "";

        OAuth2UserInfo oAuth2UserInfo = null;

        // 소셜 카테고리 검증
        if(userRequest.getClientRegistration().getRegistrationId().equals("kakao")) {
            oAuth2UserInfo = new KakaoUserInfo(oAuth2User.getAttributes());
            providerId = oAuth2UserInfo.getProviderId();
            userName = oAuth2UserInfo.getName();
        } else if(userRequest.getClientRegistration().getRegistrationId().equals("naver")) {
            oAuth2UserInfo = new NaverUserInfo(oAuth2User.getAttributes());
            providerId = oAuth2UserInfo.getProviderId();
            userName = oAuth2UserInfo.getName();
        } else {
            System.out.println("카카오와 네이버 로그인만 지원합니다.");
        }

        String provider = oAuth2UserInfo.getProvider();
        // ex) userId = kakao_1234212
        String userId = provider + "_" + providerId;
        String password = passwordEncoder.encode(oAuth2UserInfo.getName());
        String phone = "";
        String email = oAuth2UserInfo.getEmail();
        String addr = "";
        String type = "USER";
        String useYn = "Y";

        // 사용자가 이미 소셜 로그인한 기록이 있는지 검사할 객체
        User user;

        if(userRepository.findById(userId).isPresent()) {
            // 이미 소셜 로그인한 기록이 존재하면, 정보를 user 엔티티에 담아줌
            user = userRepository.findById(userId).get();
        } else {
            // 소셜 로그인한 기록이 없으면, 회원가입 처리
            user = User.builder()
                    .userId(userId)
                    .userPw(password)
                    .userNm(userName)
                    .userPhone(phone)
                    .userEmail(email)
                    .userAddr(addr)
                    .userType(type)
                    .userRgstDate(LocalDateTime.now())
                    .userModfDate(LocalDateTime.now())
                    .userUseYn(useYn)
                    .build();

            // 추가 입력 사항이 있으면 User 엔티티를 가지고
            // 추가 입력 페이지로 이동
            // 추가 입력 사항이 입력된 후 회원가입 처리
            userRepository.save(user);
        }

        // SecurityContext 에 인증 정보 저장
        return CustomUserDetails.builder()
                .user(user)
                .attributes(oAuth2User.getAttributes())
                .build();
    }
}
