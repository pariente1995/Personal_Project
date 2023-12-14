package com.saeahga.community.controller;

import com.saeahga.community.dto.UserDTO;
import com.saeahga.community.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/getUser/{userId}")
    public Map<String, Object> getUser(@PathVariable String userId) {
        Map<String, Object> resultMap = new HashMap();
        resultMap = userService.getUser(userId);

        return resultMap;
    }
}
