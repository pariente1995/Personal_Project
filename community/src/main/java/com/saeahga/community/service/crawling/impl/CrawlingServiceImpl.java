package com.saeahga.community.service.crawling.impl;

import com.saeahga.community.dto.NewsCrawlingDTO;
import com.saeahga.community.service.crawling.CrawlingService;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class CrawlingServiceImpl implements CrawlingService {
    // 현재일자로 저출산 관련 뉴스 리스트 크롤링
    @Override
    public List<NewsCrawlingDTO> getNewsCrawlingList(String year, String month, String day) {
        List<NewsCrawlingDTO> returnNewsCrawlingList = new ArrayList<>();

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

            // 뉴스 40개 크롤링될 정도로 스크롤하기
            // 아래에서 최대 30개만 담음
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
                크롤링한 뉴스 리스트 담기(최대 30개만)
                - 30개가 크롤링되지 않았다면 크롤링한 개수만큼만 담김
            */
            int cnt = 30;

            if(newsIdList.size() < 30) {
                cnt = newsIdList.size();
            }

            for(int i=0; i<cnt; i++) {
                NewsCrawlingDTO newsCrawlingDTO = NewsCrawlingDTO.builder()
                        .newsId(newsIdList.get(i).getAttribute("id"))
                        .newsTitle(newsTitleList.get(i).getAttribute("title"))
                        // 뉴스 내용 50자까지만 자르기
                        .newsDsc(newsDscAndLinkList.get(i).getText().substring(0, 50))
                        .newsLink(newsDscAndLinkList.get(i).getAttribute("href"))
                        .newsMedia(newsMediaList.get(i).getText().replace("언론사 선정", ""))
                        .build();

                returnNewsCrawlingList.add(newsCrawlingDTO);
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

            for(NewsCrawlingDTO dto : returnNewsCrawlingList) {
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
            driver.quit();
        }

        return returnNewsCrawlingList;
    }
}
