package com.saeahga.community.controller;

import com.saeahga.community.dto.DmstMrgBrkApiResultDTO;
import com.saeahga.community.dto.DmstMrgBrkDTO;
import com.saeahga.community.service.api.ApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/brokerage")
// @RequiredArgsConstructor 는 초기화 되지않은 final 필드나, @NonNull 이 붙은 필드에 대해 생성자를 생성해준다.
@RequiredArgsConstructor
public class DmstMrgBrkController {
    private final ApiService apiService;

    // 국내 결혼 중개업 화면으로 이동
    @GetMapping("/getDmstMrgBrkListView")
    public ModelAndView getDmstMrgBrkListView() {
        /*
            처음 국내 결혼 중개업 화면으로 이동 시,
            apiResultCode 값이 없기에 기본값으로 설정해준다.
        */
        DmstMrgBrkApiResultDTO returnDmstMrgBrkApiResultDTO = DmstMrgBrkApiResultDTO.builder()
                .apiResultCode("a-1")
                .build();

        ModelAndView mv = new ModelAndView();

        // templates 폴더 안에서, brokerage 폴더 아래 dmst_mrg_brk_list.html
        mv.setViewName("brokerage/dmst_mrg_brk_list");
        mv.addObject("getApiResult", returnDmstMrgBrkApiResultDTO);

        return mv;
    }

    /*
        국내 결혼 중개업 리스트 조회
        - 최대 10곳만 조회
    */
    @GetMapping("/getDmstMrgBrkList")
    public ModelAndView getDmstMrgBrkList(DmstMrgBrkDTO dmstMrgBrkDTO) throws UnsupportedEncodingException {
        // 검색 후 조회조건이 초기화 되기에, 화면으로 다시 리턴할 조회조건 값
        String returnCtpvNm; // 시도명
        String returnSggNm; // 시군구명
        String returnEntrpsNm; // 업체명
        
        if(!dmstMrgBrkDTO.getCtpvNm().equals("")) {
            returnCtpvNm = dmstMrgBrkDTO.getCtpvNm();
        } else {
            returnCtpvNm = null;
        }

        if(!dmstMrgBrkDTO.getSggNm().equals("")) {
            returnSggNm = dmstMrgBrkDTO.getSggNm();
        } else {
            returnSggNm = null;
        }

        if(!dmstMrgBrkDTO.getEntrpsNm().equals("")) {
            returnEntrpsNm = dmstMrgBrkDTO.getEntrpsNm();
        } else {
            returnEntrpsNm = null;
        }

        // 국내 결혼 중개업 API 호출
        Map<String, Object> returnApiMap = apiService.dataDmstMrgBrkAPI(dmstMrgBrkDTO, 1, 10);

        // 국내 결혼 중개업 리스트
        List<DmstMrgBrkDTO> getDmstMrgBrkList = (List<DmstMrgBrkDTO>)returnApiMap.get("dataDmstMrgBrkList");
        DmstMrgBrkApiResultDTO getApiResult = (DmstMrgBrkApiResultDTO)returnApiMap.get("apiResult");

        ModelAndView mv = new ModelAndView();

        // templates 폴더 안에서, brokerage 폴더 아래 dmst_mrg_brk_list.html
        mv.setViewName("brokerage/dmst_mrg_brk_list");
        mv.addObject("returnCtpvNm", returnCtpvNm);
        mv.addObject("returnSggNm", returnSggNm);
        mv.addObject("returnEntrpsNm", returnEntrpsNm);
        mv.addObject("getDmstMrgBrkList", getDmstMrgBrkList);
        mv.addObject("getApiResult", getApiResult);

        return mv;
    }
}
