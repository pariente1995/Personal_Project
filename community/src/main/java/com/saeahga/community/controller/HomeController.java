package com.saeahga.community.controller;

import com.saeahga.community.dto.BenefitCrawlingDTO;
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
@RequestMapping("/home")
// @RequiredArgsConstructor 는 초기화 되지않은 final 필드나, @NonNull 이 붙은 필드에 대해 생성자를 생성해준다.
@RequiredArgsConstructor
public class HomeController {
    private final CrawlingService crawlingService;

    // 메인 화면으로 이동 및 뉴스&블로그 크롤링 조회
    @GetMapping("/main")
    public ModelAndView moveMain() {
        // 현재일자
        LocalDateTime today = LocalDateTime.now();

        // 현재일자 형식 셋팅(ex. 2024.03.10)
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("YYYY.MM.dd");
        String currentDate = today.format(dateTimeFormatter);

        String month = Integer.toString(today.getMonthValue()); // 월
        String day = Integer.toString(today.getDayOfMonth()); // 일
        String dayOfWeek = today.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN); // 요일

        // 뉴스일자
        String newsDate = String.format("%s.%s(%s)", month, day, dayOfWeek);

        // 화면으로 전달할 뉴스 리스트
        // 현재일자로 크롤링하여 뉴스 리스트 조회
        List<NewsCrawlingDTO> getNewsCrawlingList = crawlingService.getNewsCrawlingList(currentDate);

        // 화면으로 전달할 출산 혜택 정보 블로그 리스트
        // 현재연도로 크롤링하여 출산 혜택 정보 블로그 리스트 조회
        List<BenefitCrawlingDTO> getBenefitCrawlingList = crawlingService.getBenefitCrawlingList(currentDate);


        ModelAndView mv = new ModelAndView();

        mv.setViewName("main");
        mv.addObject("getNewsCrawlingList", getNewsCrawlingList);
        mv.addObject("getBenefitCrawlingList", getBenefitCrawlingList);
        mv.addObject("newsDate", newsDate);

        return mv;
    }
}
