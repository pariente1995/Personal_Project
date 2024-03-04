package com.saeahga.community.service.crawling;

import com.saeahga.community.dto.NewsCrawlingDTO;

import java.util.List;

public interface CrawlingService {
    // 현재일자로 저출산 관련 뉴스 리스트 크롤링
    List<NewsCrawlingDTO> getNewsCrawlingList(String year, String month, String day);
}
