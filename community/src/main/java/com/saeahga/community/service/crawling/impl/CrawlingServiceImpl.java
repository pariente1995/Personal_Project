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
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class CrawlingServiceImpl implements CrawlingService {
    // 현재일자로 저출산 관련 뉴스 리스트 크롤링(10개) -> Jsoup 사용
    @Override
    public List<NewsCrawlingDTO> getNewsJsoupCrawlingList(String currentDate) {
        // 뉴스 크롤링 결과 리스트
        List<NewsCrawlingDTO> returnNewsJsoupCrawlingList = new ArrayList<>();

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

                returnNewsJsoupCrawlingList.add(newsCrawlingDTO);
            }

            /*
                뉴스 이미지 경로 담기
                - 바로 위의 코드에서 newsImgPath 를 같이 셋팅하지 않은 이유
                    -> 뉴스 이미지 경로를 크롤링 했을 경우 이미지가 없는 뉴스는 포함되지 않기에, 전체 뉴스 리스트와 이미지 경로의 개수가 다르기 때문에
                        ex) 전체 뉴스 리스트: 10개, 뉴스 이미지 경로: 7개

                따라서, 이미지가 있는 뉴스만 이미지 경로를 셋팅해준다.
            */
            for(NewsCrawlingDTO dto : returnNewsJsoupCrawlingList) {
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

        return returnNewsJsoupCrawlingList;
    }

    // 현재일자로 저출산 관련 뉴스 리스트 크롤링(30개) -> selenium 사용
    @Override
    public List<NewsCrawlingDTO> getNewsSeleniumCrawlingList(String year, String month, String day) throws IOException {
        // 뉴스 크롤링 결과 리스트
        List<NewsCrawlingDTO> returnNewsSeleniumCrawlingList = new ArrayList<>();

        // 크롤링할 사이트의 URL
        String baseUrl = "https://search.naver.com/search.naver?ssc=tab.news.all&where=news&sm=tab_jum&query=저출산";

        // === 브라우저 옵션 START ===
        ChromeOptions chromeOptions = new ChromeOptions();

        // 페이지 로드 전략 설정
        chromeOptions.setPageLoadStrategy(PageLoadStrategy.NORMAL);

        // 창 띄우지 않기
        chromeOptions.addArguments("headless");

        // 전체화면으로 실행
        chromeOptions.addArguments("--start-maximized");

        // 팝업 무시
        chromeOptions.addArguments("--disable-popup-blocking");

        // 보안 기능 비활성화 (샌드박스라는 공간을 비활성화 시킨다는 뜻)
        chromeOptions.addArguments("--no-sandbox");

        // /dev/shm(공유메모리) 디렉토리를 사용하지 않는다는 뜻
        chromeOptions.addArguments("--disable-dev-shm-usage");

        // GPU 사용하지 않기
        chromeOptions.addArguments("--disable-gpu");
        // === 브라우저 옵션 END ===


        // 셀레니움 브라우저 드라이버 -> 크롬
        WebDriver driver = new ChromeDriver(chromeOptions);

        try {
            driver.get(baseUrl); // 해당 url 로 접속

            // 웹페이지 전체가 로딩될 때까지 최대 3초 기다림
            // 웹페이지 로딩이 완료되면 바로 다음 명령어를 실행하여 실행 시간 단축
            driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);

            /*
                검색 옵션
                -> 현재일자로 검색되도록 설정
            */

            // === 옵션 클릭 ===
            driver.findElement(By.xpath("//*[@id=\"snb\"]/div[1]/div/div[2]/a")).click();

            // === 직접입력 클릭 ===
            driver.findElement(By.xpath("//*[@id=\"snb\"]/div[2]/ul/li[3]/div/div[1]/a[9]")).click();

            // === 년/월/일 클릭 ===
            // 화면상 기간의 "년(Year)" 옵션 중 첫번째 연도
            String firstYear = driver.findElement(By.xpath("//*[@id=\"snb\"]/div[2]/ul/li[3]/div/div[3]/div[2]/div[1]/div/div/div/ul/li[1]/a"))
                    .getText();

            // 현재연도 index
            int currentYearIndex = Integer.parseInt(year) - Integer.parseInt(firstYear) + 1;

            // 년 클릭
            driver.findElement(By.xpath(
                    String.format("//*[@id=\"snb\"]/div[2]/ul/li[3]/div/div[3]/div[2]/div[1]/div/div/div/ul/li[%s]/a", currentYearIndex))).click();

            // 월 클릭
            driver.findElement(By.xpath(
                    String.format("//*[@id=\"snb\"]/div[2]/ul/li[3]/div/div[3]/div[2]/div[2]/div/div/div/ul/li[%s]/a", month))).click();

            // 일 클릭
            driver.findElement(By.xpath(
                    String.format("//*[@id=\"snb\"]/div[2]/ul/li[3]/div/div[3]/div[2]/div[3]/div/div/div/ul/li[%s]/a", day))).click();

            // TO date 클릭
            driver.findElement(By.xpath("//*[@id=\"snb\"]/div[2]/ul/li[3]/div/div[3]/div[1]/span[3]/a")).click();

            // 년 클릭
            driver.findElement(By.xpath(
                    String.format("//*[@id=\"snb\"]/div[2]/ul/li[3]/div/div[3]/div[2]/div[1]/div/div/div/ul/li[%s]/a", currentYearIndex))).click();

            // 월 클릭
            driver.findElement(By.xpath(
                    String.format("//*[@id=\"snb\"]/div[2]/ul/li[3]/div/div[3]/div[2]/div[2]/div/div/div/ul/li[%s]/a", month))).click();

            // 일 클릭
            driver.findElement(By.xpath(
                    String.format("//*[@id=\"snb\"]/div[2]/ul/li[3]/div/div[3]/div[2]/div[3]/div/div/div/ul/li[%s]/a", day))).click();

            // 적용 버튼 클릭
            driver.findElement(By.xpath("//*[@id=\"snb\"]/div[2]/ul/li[3]/div/div[3]/div[3]/button")).click();


            /*
                네이버 썸네일의 경우 스크롤 위치에 따라 이미지가 로드되기에 스크롤을 해주어야 한다.
                - 뉴스 40개 크롤링될 정도로 스크롤
                - 이후 아래에서 최대 30개만 담음
            */
            for(int i=0; i<14; i++) {
                // 550px 만큼 아래로 스크롤
                ((JavascriptExecutor)driver).executeScript("window.scrollBy(0,550)");
                Thread.sleep(100);
            }

            // 전체의 뉴스 아이디 리스트(이미지가 없는 뉴스도 포함)
            List<WebElement> newsIdList = driver.findElements(By.cssSelector(".list_news .bx"));

            // 뉴스 제목 리스트
            List<WebElement> newsTitleList = driver.findElements(By.cssSelector(".news_contents .news_tit"));

            // 뉴스 설명 & 뉴스 링크 리스트
            List<WebElement> newsDscAndLinkList = driver.findElements(By.cssSelector(".dsc_txt_wrap"));

            // 뉴스 매체(언론사) 리스트
            List<WebElement> newsMediaList = driver.findElements(By.cssSelector(".info_group .info:nth-child(1)"));

            // 뉴스 이미지 경로 리스트(이미지가 있는 뉴스들)
            List<WebElement> newsImgPathList = driver.findElements(By.cssSelector(".news_contents .dsc_thumb .thumb"));


            // 뉴스 아이디에 해당하는 이미지 경로 데이터 담기(이미지가 있는 뉴스의 이미지 경로)
            Map<String, List<String>> newsImgMap = new HashMap<>();

            for(WebElement ele : newsImgPathList) {
                List<String> imgPathInfo = new ArrayList<>();

                // 뉴스 이미지 경로
                imgPathInfo.add(ele.getAttribute("src"));

                /*
                    네이버 썸네일의 경우 스크롤 위치에 따라 이미지가 로드되며,
                    그 이전에는 src 속성에 "data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7" 의
                    empty 값이 할당된다.
                    따라서 크롤링 시 이미지 경로가 empty 값과 같다면, data-lazysrc 속성값으로 이미지 경로를 셋팅해주어야 하기에
                    함께 담아준다.
                */
                imgPathInfo.add(ele.getAttribute("data-lazysrc"));

                // newsId에 해당하는 이미지 경로
                newsImgMap.put(ele.findElement(By.xpath("ancestor::li")).getAttribute("id"), imgPathInfo);
            }


            /*
                크롤링한 뉴스 리스트 담기
                - 최대 30개만(30개가 크롤링되지 않았다면 크롤링한 개수만큼만 담김)
            */
            int cnt = 30;

            if(newsIdList.size() < cnt) {
                cnt = newsIdList.size();
            }

            for(int i=0; i<cnt; i++) {
                NewsCrawlingDTO newsCrawlingDTO = NewsCrawlingDTO.builder()
                        .newsId(newsIdList.get(i).getAttribute("id"))
                        .newsTitle(newsTitleList.get(i).getAttribute("title"))
                        .newsDsc(newsDscAndLinkList.get(i).getText())
                        .newsLink(newsDscAndLinkList.get(i).getAttribute("href"))
                        .newsMedia(newsMediaList.get(i).getText().replace("언론사 선정", ""))
                        .build();

                // 뉴스 내용 80자까지만 자르기
                if(newsCrawlingDTO.getNewsDsc().length() > 80)
                    newsCrawlingDTO.setNewsDsc(newsCrawlingDTO.getNewsDsc().substring(0, 80));

                returnNewsSeleniumCrawlingList.add(newsCrawlingDTO);
            }

            /*
                뉴스 이미지 경로 담기
                - 바로 위의 코드에서 newsImgPath 를 같이 셋팅하지 않은 이유
                    -> 뉴스 이미지 경로를 크롤링 했을 경우 이미지가 없는 뉴스는 포함되지 않기에, 전체 뉴스 리스트와 이미지 경로의 개수가 다르기 때문에
                        ex) 전체 뉴스 리스트: 30개, 뉴스 이미지 경로: 27개

                따라서, 이미지가 있는 뉴스만 이미지 경로를 셋팅해준다.
            */
            // 네이버 이미지 src 속성의 empty 값
            String emptySrc = "data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7";

            for(NewsCrawlingDTO dto : returnNewsSeleniumCrawlingList) {
                String newsId = dto.getNewsId();

                if(newsImgMap.containsKey(newsId)) {
                    if(newsImgMap.get(newsId).get(0).equals(emptySrc)) { // 스크롤되지 않아 뉴스 이미지 경로가 empty 값으로 셋팅된 경우
                        dto.setNewsImgPath(newsImgMap.get(newsId).get(1));
                    } else {
                        dto.setNewsImgPath(newsImgMap.get(newsId).get(0));
                    }
                } else { // 이미지가 없는 뉴스일 경우
                    dto.setNewsImgPath("no img");
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            /*
                셀레니움 실행 후, 작업관리자의 백그라운드에 크롬 프로세스가 계속해서 쌓이고
                CPU 사용량이 늘어나 느려지기에 chromedriver.exe 프로세스를 강제로 종료해준다.
                -> 하지만 5번 정도를 반복해야지만 문제가 해결되었다..
            */
            for(int i=0; i<5; i++) {
                Runtime.getRuntime().exec("taskkill /f /im chromedriver.exe /t");
            }

            driver.quit();
        }

        return returnNewsSeleniumCrawlingList;
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
