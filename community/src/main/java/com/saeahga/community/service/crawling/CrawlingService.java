package com.saeahga.community.service.crawling;

import com.saeahga.community.dto.BenefitCrawlingDTO;
import com.saeahga.community.dto.NewsCrawlingDTO;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface CrawlingService {
    // 현재일자로 저출산 관련 뉴스 리스트 크롤링(10개) -> Jsoup 사용
    List<NewsCrawlingDTO> getNewsJsoupCrawlingList(String currentDate);

    // 현재일자로 저출산 관련 뉴스 리스트 크롤링(30개) -> selenium 사용
    List<NewsCrawlingDTO> getNewsSeleniumCrawlingList(String year, String month, String day) throws IOException;

    // 현재연도로 출산 혜택 정보 관련 블로그 리스트 크롤링
    List<BenefitCrawlingDTO> getBenefitCrawlingList(String currentDate);

    // 페이지별 맘카페 리스트 크롤링
    Map<String, Object> getMomCafeCrawlingList(int pageNo);
}
