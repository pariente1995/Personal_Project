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
import java.util.List;

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

        // 현재일자 형식 셋팅(ex. 2024.03.10)
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("YYYY.MM.dd");
        String currentDate = today.format(dateTimeFormatter);

        /*
            1. 화면으로 전달할 뉴스 리스트
            - 현재일자로 크롤링하여 뉴스 리스트 조회
        */
        List<NewsCrawlingDTO> getNewsCrawlingList = crawlingService.getNewsCrawlingList(currentDate);

        ModelAndView mv = new ModelAndView();

        mv.setViewName("news/news_list");
        mv.addObject("getNewsCrawlingList", getNewsCrawlingList);
        return mv;
    }
}
