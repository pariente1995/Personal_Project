package com.saeahga.community.service.crawling.impl;

import com.saeahga.community.dto.BenefitCrawlingDTO;
import com.saeahga.community.dto.MomCafeCrawlingDTO;
import com.saeahga.community.dto.NewsCrawlingDTO;
import com.saeahga.community.dto.PageDTO;
import com.saeahga.community.service.crawling.CrawlingService;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CrawlingServiceImpl implements CrawlingService {
    // 현재일자로 저출산 관련 뉴스 리스트 크롤링
    @Override
    public List<NewsCrawlingDTO> getNewsCrawlingList(String currentDate) {
        // 뉴스 크롤링 결과 리스트
        List<NewsCrawlingDTO> returnNewsCrawlingList = new ArrayList<>();

        // 크롤링할 사이트의 URL
        String baseUrl = "https://search.naver.com/search.naver?where=news&query=%EC%A0%80%EC%B6%9C%EC%82%B0&sm=tab_opt&sort=0&photo=0&field=0&pd=3&ds="
                + currentDate + "&de=" + currentDate + "&docid=&related=0&mynews=0&office_type=0&office_section_code=0&news_office_checked=&nso=so%3Ar%2Cp%3Afrom"
                + currentDate.replace(".", "") + "to"
                + currentDate.replace(".", "") + "&is_sug_officeid=0&office_category=0&service_area=0";

        // Jsoup 커넥션 생성
        Connection conn = Jsoup.connect(baseUrl);

        try {
            // baseUrl 의 내용을 HTML Document 객체로 가져온다.
            Document docs = conn.get();

            // 전체의 뉴스 아이디 리스트(이미지가 없는 뉴스도 포함)
            Elements newsIdList = docs.select(".list_news .bx");

            // 뉴스 제목 리스트
            Elements newsTitleList = docs.select(".news_contents .news_tit");

            // 뉴스 설명 & 뉴스 링크 리스트
            Elements newsDscAndLinkList = docs.select(".dsc_txt_wrap");

            // 뉴스 매체(언론사) 리스트
            Elements newsMediaList = docs.select(".info_group .info:nth-child(1)");

            // 뉴스 이미지 경로 리스트(이미지가 있는 뉴스들)
            Elements newsImgPathList = docs.select(".news_contents .dsc_thumb .thumb");

            // 뉴스 아이디에 해당하는 이미지 경로 데이터 담기(이미지가 있는 뉴스의 이미지 경로)
            Map<String, String> newsImgMap = new HashMap<>();

            for(Element ele : newsImgPathList) {
                /*
                    네이버 썸네일의 경우 스크롤 위치에 따라 이미지가 로드되며,
                    그 이전에는 src 속성에 "data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7" 의
                    empty 값이 할당된다.
                    하지만 Jsoup 의 경우 동적 웹페이지 크롤링이 불가하기에, data-lazysrc 속성값으로 이미지 경로를 셋팅해준다.
                */
                // newsId에 해당하는 뉴스 이미지 경로
                newsImgMap.put(ele.parents().select(".bx").attr("id"), ele.attr("data-lazysrc"));
            }


            /*
                크롤링한 뉴스 리스트 담기
                - 기본적으로 10개가 크롤링되지만, 10개가 크롤링되지 않았다면 크롤링한 개수만큼만 담김
            */
            for(int i=0; i<newsIdList.size(); i++) {
                NewsCrawlingDTO newsCrawlingDTO = NewsCrawlingDTO.builder()
                        .newsId(newsIdList.get(i).attr("id"))
                        .newsTitle(newsTitleList.get(i).attr("title"))
                        .newsDsc(newsDscAndLinkList.get(i).text())
                        .newsLink(newsDscAndLinkList.get(i).attr("href"))
                        .newsMedia(newsMediaList.get(i).text().replace("언론사 선정", ""))
                        .build();

                // 뉴스 내용 50자까지만 자르기
                if(newsCrawlingDTO.getNewsDsc().length() > 50)
                    newsCrawlingDTO.setNewsDsc(newsCrawlingDTO.getNewsDsc().substring(0, 50));

                returnNewsCrawlingList.add(newsCrawlingDTO);
            }

            /*
                뉴스 이미지 경로 담기
                - 바로 위의 코드에서 newsImgPath 를 같이 셋팅하지 않은 이유
                    -> 뉴스 이미지 경로를 크롤링 했을 경우 이미지가 없는 뉴스는 포함되지 않기에, 전체 뉴스 리스트와 이미지 경로의 개수가 다르기 때문에
                        ex) 전체 뉴스 리스트: 10개, 뉴스 이미지 경로: 7개

                따라서, 이미지가 있는 뉴스만 이미지 경로를 셋팅해준다.
            */
            for(NewsCrawlingDTO dto : returnNewsCrawlingList) {
                String newsId = dto.getNewsId();

                if(newsImgMap.containsKey(newsId)) {
                    dto.setNewsImgPath(newsImgMap.get(newsId));
                } else { // 이미지가 없는 뉴스일 경우
                    dto.setNewsImgPath("no img");
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        }

        return returnNewsCrawlingList;
    }

    // 현재연도로 출산 혜택 정보 관련 블로그 리스트 크롤링
    @Override
    public List<BenefitCrawlingDTO> getBenefitCrawlingList(String currentDate) {
        // 출산 혜택 크롤링 결과 리스트
        List<BenefitCrawlingDTO> returnBenefitCrawlingList = new ArrayList<>();

        // 크롤링할 사이트의 URL
        String baseUrl = "https://search.naver.com/search.naver?ssc=tab.blog.all&query=" + currentDate.substring(0, 4) + "%EB%85%84%20%EC%B6%9C%EC%82%B0%20%ED%98%9C%ED%83%9D&sm=tab_opt&nso=so%3Ar%2Cp%3Aall";

        // Jsoup 커넥션 생성
        Connection conn = Jsoup.connect(baseUrl);

        try {
            // baseUrl 의 내용을 HTML Document 객체로 가져온다.
            Document docs = conn.get();

            // 출산 혜택 정보 블로그 제목 & 링크 리스트
            Elements benefitTitleAndLinkList = docs.select(".title_area .title_link");

            // 출산 혜택 정보 블로그 이름 리스트
            Elements benefitBlogNmList = docs.select(".user_info .name");


            /*
                크롤링한 출산 혜택 정보 블로그 리스트 담기
                - 최대 5개만(혹시 5개가 크롤링되지 않았다면 크롤링한 개수만큼만 담김)
            */
            int cnt = 5;

            if(benefitTitleAndLinkList.size() < cnt) {
                cnt = benefitTitleAndLinkList.size();
            }

            for(int i=0; i<cnt; i++) {
                BenefitCrawlingDTO benefitCrawlingDTO = BenefitCrawlingDTO.builder()
                        .benefitTitle(benefitTitleAndLinkList.get(i).text())
                        .benefitLink(benefitTitleAndLinkList.get(i).attr("href"))
                        .benefitBlogNm(benefitBlogNmList.get(i).text())
                        .build();

                returnBenefitCrawlingList.add(benefitCrawlingDTO);
            }
        } catch(IOException e) {
            e.printStackTrace();
        }

        return returnBenefitCrawlingList;
    }

    // 페이지별 맘카페 리스트 크롤링
    @Override
    public Map<String, Object> getMomCafeCrawlingList(int pageNo) {
        // 맘카페 크롤링 리스트 + 페이지네이션 처리 관련 map
        Map<String, Object> returnMomCafeMap = new HashMap<>();

        // 맘카페 크롤링 결과 리스트
        List<MomCafeCrawlingDTO> momCafeCrawlingDTOList = new ArrayList<>();
        // 맘카페 총 검색 건수
        int totElements = 0;

        // 크롤링할 사이트의 URL
        String baseUrl = "https://ad.search.naver.com/search.naver?where=ad&sm=svc_nrs&query=%EB%A7%98%EC%B9%B4%ED%8E%98&referenceId=iQ6Eldqo1LVssexHCfGssssssVZ-301309&bucketTest=AD-PWL-SITURL&bucket=0&pagingIndex=";

        // 실제 요청 URL
        String requestUrl = baseUrl + pageNo;

        try {
            // === 맘카페 총 검색 건수 & 총 페이지 수 구하기 START ===
            int tempPageNo = 1;

            while(true) {
                // 페이지 URL
                String pageUrl = baseUrl + tempPageNo;

                // 페이지네이션 처리를 위한 Jsoup 커넥션 생성
                Connection connPage = Jsoup.connect(pageUrl);

                // pageUrl 의 내용을 HTML Document 객체로 가져온다.
                Document docs = connPage.get();

                // 해당 페이지에 대한 맘카페 리스트
                Elements momCafeList = docs.select(".lst_type .lst");

                // 해당 페이지의 맘카페 건수 더함
                if(momCafeList.size() != 0) {
                    totElements += momCafeList.size();
                    tempPageNo++;
                } else {
                    break;
                }
            }

            // 맘카페 총 페이지 수
            int totPages = totElements/25 > 0 ? (totElements/25) + 1 : 1;

            // === 맘카페 총 검색 건수 & 총 페이지 수 구하기 END ===

            // Jsoup 커넥션 생성
            Connection conn = Jsoup.connect(requestUrl);

            // requestUrl 의 내용을 HTML Document 객체로 가져온다.
            Document docs = conn.get();

            // 맘카페 제목 & 링크 리스트
            Elements momCafeTitleAndLinkList = docs.select(".info_wrap .tit_wrap");

            // 맘카페 설명 리스트
            Elements momCafeDscList = docs.select(".desc_area .link_desc");

            // 맘카페 이미지 경로 리스트
            Elements momCafeImgPathList = docs.select(".ad_thumb .image");


            // 크롤링한 맘카페 리스트 담기
            for(int i=0; i<momCafeTitleAndLinkList.size(); i++) {
                MomCafeCrawlingDTO momCafeCrawlingDTO = MomCafeCrawlingDTO.builder()
                        .momCafeTitle(momCafeTitleAndLinkList.get(i).text())
                        .momCafeDsc(momCafeDscList.get(i).text())
                        .momCafeLink(momCafeTitleAndLinkList.get(i).attr("href"))
                        .momCafeImgPath(momCafeImgPathList.get(i).attr("src"))
                        .build();

                momCafeCrawlingDTOList.add(momCafeCrawlingDTO);
            }

            // 페이지네이션 처리 관련 데이터 담기
            PageDTO pageDTO = PageDTO.builder()
                    .totalElements(totElements)
                    .pageNumber(pageNo)
                    .pageRange(5)
                    .totalPages(totPages)
                    .build();

            // 페이지 리스트 범위는 최대 10까지만 설정되도록
            if(pageDTO.getPageRange() > 10)
                pageDTO.setPageRange(10);

            returnMomCafeMap.put("momCafeCrawlingDTOList", momCafeCrawlingDTOList);
            returnMomCafeMap.put("pageDTO", pageDTO);
        } catch(IOException e) {
            e.printStackTrace();
        }

        return returnMomCafeMap;
    }
}
