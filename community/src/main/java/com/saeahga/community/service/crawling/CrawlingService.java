package com.saeahga.community.service.crawling;

import com.saeahga.community.dto.BenefitCrawlingDTO;
import com.saeahga.community.dto.MomCafeCrawlingDTO;
import com.saeahga.community.dto.NewsCrawlingDTO;

import java.util.List;
import java.util.Map;

public interface CrawlingService {
    // 현재일자로 저출산 관련 뉴스 리스트 크롤링
    List<NewsCrawlingDTO> getNewsCrawlingList(String currentDate);

    // 현재연도로 출산 혜택 정보 관련 블로그 리스트 크롤링
    List<BenefitCrawlingDTO> getBenefitCrawlingList(String currentDate);

    // 페이지별 맘카페 리스트 크롤링
    Map<String, Object> getMomCafeCrawlingList(int pageNo);
}
