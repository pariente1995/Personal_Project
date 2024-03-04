package com.saeahga.community.controller;

import com.saeahga.community.dto.NewsCrawlingDTO;
import com.saeahga.community.service.crawling.CrawlingService;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.WebElement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/home")
// @RequiredArgsConstructor 는 초기화 되지않은 final 필드나, @NonNull 이 붙은 필드에 대해 생성자를 생성해준다.
@RequiredArgsConstructor
public class HomeController {
    private final CrawlingService crawlingService;

    @GetMapping("/main")
    public ModelAndView moveMain() {
        // 현재일자
        LocalDateTime currentDate = LocalDateTime.now();

        String year = Integer.toString(currentDate.getYear()); // 년
        String month = Integer.toString(currentDate.getMonthValue()); // 월
        String day = Integer.toString(currentDate.getDayOfMonth()); // 일
        String dayOfWeek = currentDate.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN); // 요일
        String newsDate = month + "." + day + "(" + dayOfWeek + ")";

        // 화면으로 전달할 뉴스 리스트
        // 현재일자로 크롤링하여 뉴스 리스트 조회
        List<NewsCrawlingDTO> getNewsCrawlingList = crawlingService.getNewsCrawlingList(year, month, day);

        ModelAndView mv = new ModelAndView();
        mv.setViewName("main");
        mv.addObject("getNewsCrawlingList", getNewsCrawlingList);
        mv.addObject("newsDate", newsDate);

        return mv;
    }
}
