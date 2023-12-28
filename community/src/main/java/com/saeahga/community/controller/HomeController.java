package com.saeahga.community.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/home")
public class HomeController {
    @GetMapping("/main")
    public ModelAndView moveMain() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("main");

        return mv;
    }
}
