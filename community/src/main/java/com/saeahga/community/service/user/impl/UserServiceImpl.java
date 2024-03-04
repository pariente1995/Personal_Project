package com.saeahga.community.service.user.impl;

import com.saeahga.community.entity.User;
import com.saeahga.community.mapper.UserMapper;
import com.saeahga.community.repository.UserRepository;
import com.saeahga.community.service.mail.MailService;
import com.saeahga.community.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Date;
import java.util.List;


@Service
// @RequiredArgsConstructor 는 초기화 되지않은 final 필드나, @NonNull 이 붙은 필드에 대해 생성자를 생성해준다.
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final MailService mailService;

    // @RequiredArgsConstructor 어노테이션을 사용하여 생성자 주입 시, 아래와 같이 생성자 생성 부분을 대신해준다.
//    @Autowired
//    public UserServiceImpl(UserMapper userMapper) {
//        this.userMapper = userMapper;
//    }

    // 사용자 조회
    @Override
    public User getUser(String userId) {
        return userRepository.findById(userId).get();
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

    // 비밀번호 찾기(아이디와 이메일로)
    @Override
    public User findPw(User user) {
        if(userRepository.findByUserIdAndUserEmail(user.getUserId(), user.getUserEmail()).isEmpty()) {
            return null;
        } else {
            return userRepository.findByUserIdAndUserEmail(user.getUserId(), user.getUserEmail()).get();
        }
    }

    // 인증번호 생성 + 메일로 인증번호 전송
    @Override
    public String submitCode(User user) {
        // 인증번호 생성(8자리)
        char[] charSet = new char[]
                { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                    'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K',
                    'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
                    'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g',
                    'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
                    's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '!', '@', '#',
                    '$', '%', '^', '&' };

        StringBuffer sb = new StringBuffer();
        SecureRandom sr = new SecureRandom();

        sr.setSeed(new Date().getTime());

        int idx = 0;
        int len = charSet.length;

        for (int i=0; i<8; i++) {
            idx = sr.nextInt(len); // 강력한 난수를 발생시키기 위해 SecureRandom 을 사용한다.
            sb.append(charSet[idx]);
        }

        String certificationNum = sb.toString();

        String mailTitle = "SAEAHGA : 비밀번호 찾기 인증번호 안내";
        String mailBody = "인증번호 : " + certificationNum;

        // 메일 전송 부분
        mailService.send(user.getUserEmail(), mailTitle, mailBody);

        return certificationNum;
    }

    // 새 비밀번호 업데이트
    @Override
    public void updatePw(User user) {
        userRepository.updatePw(user);
    }

    // 회원정보 수정
    @Override
    public void updateUserInfo(User user) {
        userRepository.updateUserInfo(user);
    }
}
