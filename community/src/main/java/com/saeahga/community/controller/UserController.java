package com.saeahga.community.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saeahga.community.dto.ResponseDTO;
import com.saeahga.community.dto.UserDTO;
import com.saeahga.community.entity.CustomUserDetails;
import com.saeahga.community.entity.User;
import com.saeahga.community.service.user.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.DoubleStream;

@RestController
@RequestMapping("/user")
// @RequiredArgsConstructor 는 초기화 되지않은 final 필드나, @NonNull 이 붙은 필드에 대해 생성자를 생성해준다.
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    // 테스트
    /*
    @GetMapping("/getUser/{userId}")
    public Map<String, Object> getUser(@PathVariable String userId) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap = userService.getUser(userId);

        return resultMap;
    }
    */

    // 로그인 화면으로 이동
    @GetMapping("/login")
    public ModelAndView loginView() {
        ModelAndView mv = new ModelAndView();

        // templates 폴더 안에서, user 폴더 아래 login.html
        mv.setViewName("user/login");

        return mv;
    }
    
    // 회원가입 화면으로 이동
    @GetMapping("/join")
    public ModelAndView joinView() {
        ModelAndView mv = new ModelAndView();
        
        mv.setViewName("user/join");
        
        return mv;
    }

    // 아이디 중복체크
    @PostMapping("/idCheck")
    public ResponseEntity<?> idCheck(UserDTO userDTO) {
        ResponseDTO<Map<String, String>> responseDTO = new ResponseDTO<>();
        Map<String, String> returnMap = new HashMap<String, String>();

        try {
            User user = User.builder()
                            .userId(userDTO.getUserId())
                            .build();

            User checkedUser = userService.idCheck(user);

            if(checkedUser != null) {
                returnMap.put("msg", "duplicatedId");
            } else {
                returnMap.put("msg", "idOk");
            }

            responseDTO.setItem(returnMap);

            return ResponseEntity.ok().body(responseDTO);
        } catch(Exception e) {
            responseDTO.setErrorMessage(e.getMessage());
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    // 회원가입
    @PostMapping("/join")
    public ResponseEntity<?> join(UserDTO userDTO) {
        ResponseDTO<Map<String, String>> responseDTO = new ResponseDTO<>();
        Map<String, String> returnMap = new HashMap<String, String>();

        try {
            User user = User.builder()
                    .userId(userDTO.getUserId())
                    .userPw(passwordEncoder.encode(userDTO.getUserPw()))
                    .userNm(userDTO.getUserNm())
                    .userPhone(userDTO.getUserPhone())
                    .userEmail(userDTO.getUserEmail())
                    .userAddr(userDTO.getUserAddr())
                    .userType("USER")
                    .userRgstDate(LocalDateTime.now())
                    .userModfDate(LocalDateTime.now())
                    .userUseYn("Y")
                    .build();

            // 회원가입 진행
            User returnUser = userService.join(user);

            returnMap.put("joinMsg", "ok");

            responseDTO.setItem(returnMap);
            return ResponseEntity.ok().body(responseDTO);
        } catch(Exception e) {
            returnMap.put("joinMsg", "fail");
            responseDTO.setItem(returnMap);
            responseDTO.setErrorMessage(e.getMessage());

            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    // 아이디 찾기 화면으로 이동
    @GetMapping("/findIdPrev")
    public ModelAndView findIdPrevView() {
        ModelAndView mv = new ModelAndView();

        mv.setViewName("user/find_id_prev");

        return mv;
    }

    // 아이디 찾기(이름과 이메일로)
    @PostMapping("findId")
    public ResponseEntity<?> findId(UserDTO userDTO) {
        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>();

        try {
            User user = User.builder()
                    .userNm(userDTO.getUserNm())
                    .userEmail(userDTO.getUserEmail())
                    .build();

            // 이름으로 사용자 수 조회
            int userNmCnt = userService.getUserNmCnt(user);

            Map<String, Object> returnMap = new HashMap<>();

            // 사용자 수가 없으면
            if(userNmCnt == 0) {
                returnMap.put("findIdMsg", "wrongNm");
            } else {
                // 이름, 이메일로 사용자 아이디 조회
                List<User> userIdList = userService.findId(user);

                if(userIdList.size() != 0) {
                    // 화면으로 전달할 사용자 아이디 리스트
                    List<UserDTO> getUserIdList = new ArrayList<>();

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

                    for(int i=0; i<userIdList.size(); i++) {
                        UserDTO returnUserId = UserDTO.builder()
                                .userId(userIdList.get(i).getUserId())
                                // 1. LocalDateTime -> Date 로 변환
                                // 2. 날짜 포맷 적용
                                .userRgstDate(dateFormat.format(Timestamp.valueOf(userIdList.get(i).getUserRgstDate())))
                                .build();

                        // 소셜 회원가입 구분 -> 소셜 회원가입 시, 아이디가 너무 길게 생성되기때문에
                        if(returnUserId.getUserId().contains("kakao")) {
                            returnUserId.setUserId("Kakao");
                        } else if(returnUserId.getUserId().contains("naver")) {
                            returnUserId.setUserId("Naver");
                        }

                        getUserIdList.add(returnUserId);
                    }

                    returnMap.put("findIdMsg", "infoOK");
                    returnMap.put("getUserIdList", getUserIdList);
                } else {
                    // 이메일이 잘못된 경우
                    returnMap.put("findIdMsg", "wrongEmail");
                }
            }

            responseDTO.setItem(returnMap);
            return ResponseEntity.ok().body(responseDTO);
        } catch(Exception e) {
            responseDTO.setErrorMessage(e.getMessage());
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    // 아이디 찾기 완료 화면으로 이동
    @PostMapping("/findIdNext")
    public ModelAndView findIdNextView(@RequestParam("getUserIdList") String getUserIdList) throws IOException {
        // 아이디 찾기 화면에서 찾은 사용자 아이디 리스트
        List<UserDTO> userIdList = new ObjectMapper().readValue(getUserIdList, new TypeReference<List<UserDTO>>() {});

        ModelAndView mv = new ModelAndView();

        mv.setViewName("user/find_id_next");
        mv.addObject("userIdList", userIdList);
        return mv;
    }

    // 비밀번호 찾기 화면으로 이동
    @GetMapping("/findPwPrev")
    public ModelAndView findPwPrevView() {
        ModelAndView mv = new ModelAndView();

        mv.setViewName("user/find_pw_prev");

        return mv;
    }

    // 인증번호 전송
    @PostMapping("/submitCode")
    public ResponseEntity<?> submitCode(UserDTO userDTO, HttpSession session) {
        ResponseDTO<Map<String, String>> responseDTO = new ResponseDTO<>();

        try {
            User user = User.builder()
                    .userId(userDTO.getUserId())
                    .userEmail(userDTO.getUserEmail())
                    .build();

            User getUser = userService.findPw(user);

            Map<String, String> returnMap = new HashMap<String, String>();

            if(userService.idCheck(user) == null) { // 아이디 중복체크
                // 아이디가 없을 경우
                returnMap.put("findPwMsg", "wrongId");
            } else if(getUser == null) {
                // 이메일이 일치하지 않을 경우
                returnMap.put("findPwMsg", "wrongEmail");
            } else {
                // 인증번호 생성 부분 + 메일 전송
                String certificationNum = userService.submitCode(user);

                // 세션에 인증번호 저장
                session.setAttribute("certificationNum", certificationNum);

                returnMap.put("findPwMsg", "submitCode");
            }

            responseDTO.setItem(returnMap);

            return ResponseEntity.ok().body(responseDTO);
        } catch(Exception e) {
            responseDTO.setErrorMessage(e.getMessage());
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    // 인증번호 확인
    @PostMapping("/checkCertificationNum")
    public ResponseEntity<?> checkCertificationNum(@RequestParam("certificationNum") String code, HttpSession session) {
        ResponseDTO<Map<String, String>> responseDTO = new ResponseDTO<>();
        Map<String, String> returnMap = new HashMap<String, String>();

        // 세션에 저장되어 있는 발급된 인증번호
        String sessionCode = (String)session.getAttribute("certificationNum");

        // 화면의 인증번호와 세션의 인증번호 비교
        try {
            if(code.equals(sessionCode) && sessionCode != null) {
                returnMap.put("checkCodeMsg", "codeOk");
            } else {
                returnMap.put("checkCodeMsg", "codeNo");
            }

            responseDTO.setItem(returnMap);

            return ResponseEntity.ok().body(responseDTO);
        } catch(Exception e) {
            responseDTO.setErrorMessage(e.getMessage());
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    // 새 비밀번호 설정 화면으로 이동
    @GetMapping("/findPwNext")
    public ModelAndView findPwNextView(@RequestParam("userId") String userId, HttpSession session) {
        // 세션에 사용자 아이디 저장
        session.setAttribute("userId", userId);
        
        ModelAndView mv = new ModelAndView();

        mv.setViewName("user/find_pw_next");

        return mv;
    }

    // 새 비빌번호 설정
    @PostMapping("/updatePw")
    public ResponseEntity<?> updatePw(@RequestParam("userPw") String newUserPw, HttpSession session) {
        ResponseDTO<Map<String, String>> responseDTO = new ResponseDTO<>();
        Map<String, String> returnMap = new HashMap<String, String>();

        // 세션에 저장되어 있는 사용자 아이디
        String userId = (String)session.getAttribute("userId");

        // 새 비밀번호로 업데이트
        try {
            User user = User.builder()
                    .userId(userId)
                    .userPw(passwordEncoder.encode(newUserPw))
                    .build();

            // 새 비밀번호로 업데이트
            userService.updatePw(user);

            returnMap.put("updateMsg", "updateOk");

            responseDTO.setItem(returnMap);

            return ResponseEntity.ok().body(responseDTO);
        } catch(Exception e) {
            returnMap.put("updateMsg", "updateNo");
            responseDTO.setItem(returnMap);
            responseDTO.setErrorMessage(e.getMessage());

            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    // 회원정보 수정 화면으로 이동
    @GetMapping("/myInfo")
    public ModelAndView myInfoView(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        // 사용자 아이디
        String userId = customUserDetails.getUsername();

        // 사용자 정보 조회
        User user = userService.getUser(userId);

        UserDTO userDTO = UserDTO.builder()
                .userId(user.getUserId())
                .userNm(user.getUserNm())
                .userPhone(user.getUserPhone())
                .userEmail(user.getUserEmail())
                .userAddr(user.getUserAddr())
                .build();

        ModelAndView mv = new ModelAndView();

        mv.addObject("userInfo", userDTO);
        mv.setViewName("user/my_info");

        return mv;
    }

    // 회원정보 수정
    @PostMapping("/updateMyInfo")
    public ResponseEntity<?> updateMyInfo(UserDTO userDTO) {
        ResponseDTO<Map<String, String>> responseDTO = new ResponseDTO<>();
        Map<String, String> returnMap = new HashMap<String, String>();

        // 회원정보 업데이트
        try {
            User user = User.builder()
                    .userId(userDTO.getUserId())
                    .userPw(passwordEncoder.encode(userDTO.getUserPw()))
                    .userNm(userDTO.getUserNm())
                    .userPhone(userDTO.getUserPhone())
                    .userEmail(userDTO.getUserEmail())
                    .userAddr(userDTO.getUserAddr())
                    .userModfDate(LocalDateTime.now())
                    .build();

            userService.updateUserInfo(user);

            returnMap.put("updateMyInfoMsg", "updateOk");

            responseDTO.setItem(returnMap);

            return ResponseEntity.ok().body(responseDTO);
        } catch(Exception e) {
            returnMap.put("updateMyInfoMsg", "updateNo");
            responseDTO.setItem(returnMap);
            responseDTO.setErrorMessage(e.getMessage());

            return ResponseEntity.badRequest().body(responseDTO);
        }
    }
}
