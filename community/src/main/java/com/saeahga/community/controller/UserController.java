package com.saeahga.community.controller;

import com.saeahga.community.dto.ResponseDTO;
import com.saeahga.community.dto.UserDTO;
import com.saeahga.community.entity.User;
import com.saeahga.community.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
// @RequiredArgsConstructor 는 초기화 되지않은 final 필드나, @NonNull 이 붙은 필드에 대해 생성자를 생성해준다.
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/getUser/{userId}")
    public Map<String, Object> getUser(@PathVariable String userId) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap = userService.getUser(userId);

        return resultMap;
    }

    // 로그인 화면으로 이동
    @GetMapping("/login")
    public ModelAndView login() {
        ModelAndView mv = new ModelAndView();

        // templates 폴더 안에서, user 폴더 아래 login.html
        mv.setViewName("user/login");

        return mv;
    }
    
    // 회원가입 화면으로 이동
    @GetMapping("/join")
    public ModelAndView join() {
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
            responseDTO.setErrorMessage(e.getMessage());
            responseDTO.setItem(returnMap);

            return ResponseEntity.badRequest().body(responseDTO);
        }
    }
}
