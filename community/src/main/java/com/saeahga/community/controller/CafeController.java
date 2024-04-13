package com.saeahga.community.controller;

import com.saeahga.community.dto.MomCafeCrawlingDTO;
import com.saeahga.community.dto.PageDTO;
import com.saeahga.community.service.crawling.CrawlingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cafe")
// @RequiredArgsConstructor 는 초기화 되지않은 final 필드나, @NonNull 이 붙은 필드에 대해 생성자를 생성해준다.
@RequiredArgsConstructor
public class CafeController {
    private final CrawlingService crawlingService;

    // 맘카페 리스트 조회
    @GetMapping("/getMomCafeList")
    public ModelAndView getMomCafeList(@RequestParam("pageNo") int pageNo) {
        // 화면으로 전달할 맘카페 리스트 + 페이지네이션 데이터 조회
        Map<String, Object> returnMap = crawlingService.getMomCafeCrawlingList(pageNo);

        // 페이지별 맘카페 리스트
        List<MomCafeCrawlingDTO> getMomCafeCrawlingList = (List<MomCafeCrawlingDTO>)returnMap.get("momCafeCrawlingDTOList");

        // 페이지네이션 데이터
        PageDTO getPageDTO = (PageDTO)returnMap.get("pageDTO");

        ModelAndView mv = new ModelAndView();

        mv.setViewName("cafe/mom_cafe_list");
        mv.addObject("getMomCafeCrawlingList", getMomCafeCrawlingList);
        mv.addObject("getPagination", getPageDTO);

        return mv;
    }
}
