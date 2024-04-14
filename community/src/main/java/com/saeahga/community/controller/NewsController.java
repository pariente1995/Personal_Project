package com.saeahga.community.controller;

import com.saeahga.community.dto.NewsCrawlingDTO;
import com.saeahga.community.service.crawling.CrawlingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/news")
// @RequiredArgsConstructor 는 초기화 되지않은 final 필드나, @NonNull 이 붙은 필드에 대해 생성자를 생성해준다.
@RequiredArgsConstructor
public class NewsController {
    private final CrawlingService crawlingService;

    @GetMapping("/getNewsList")
    public ModelAndView getNewsList() {
        // 현재일자
        LocalDateTime today = LocalDateTime.now();

        String year = Integer.toString(today.getYear()); // 년
        String month = Integer.toString(today.getMonthValue()); // 월
        String day = Integer.toString(today.getDayOfMonth()); // 일
        String dayOfWeek = today.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN); // 요일

        // 뉴스일자
        String newsDate = String.format("%s.%s(%s)", month, day, dayOfWeek);

        /*
            화면으로 전달할 뉴스 리스트
            - 현재일자로 크롤링하여 뉴스 리스트 조회
        */
        List<NewsCrawlingDTO> getNewsSeleniumCrawlingList = crawlingService.getNewsSeleniumCrawlingList(year, month, day);

        ModelAndView mv = new ModelAndView();

        mv.setViewName("news/news_list");
        mv.addObject("getNewsSeleniumCrawlingList", getNewsSeleniumCrawlingList);
        mv.addObject("newsDate", newsDate);

        return mv;
    }
}
