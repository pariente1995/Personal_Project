package com.saeahga.community.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/cafe")
// @RequiredArgsConstructor 는 초기화 되지않은 final 필드나, @NonNull 이 붙은 필드에 대해 생성자를 생성해준다.
@RequiredArgsConstructor
public class CafeController {
    // 맘카페 리스트 조회
    @GetMapping("/mom")
    public ModelAndView getMomCafeList() {
        ModelAndView mv = new ModelAndView();

        mv.setViewName("cafe/mom_cafe_list");
//        mv.addObject("getMomCafeList", getMomCafeList);

        return mv;
    }
}
