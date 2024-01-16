package com.saeahga.community.controller;

import com.saeahga.community.dto.ResponseDTO;
import com.saeahga.community.dto.UserDTO;
import com.saeahga.community.entity.User;
import com.saeahga.community.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

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
}
