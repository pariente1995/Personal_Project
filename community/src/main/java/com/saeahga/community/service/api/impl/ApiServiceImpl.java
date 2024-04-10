package com.saeahga.community.service.api.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saeahga.community.dto.DmstMrgBrkApiResultDTO;
import com.saeahga.community.dto.DmstMrgBrkDTO;
import com.saeahga.community.service.api.ApiService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ApiServiceImpl implements ApiService {
    // API 서비스키
    @Value("${data.domestic.marriage.brokerage.service-key}")
    private String serviceKey;

    @Value("${data.domestic.marriage.brokerage.base-url}")
    private String baseUrl;

    // 공공데이터포털 -> 국내결혼중개업 API
    // pageNo: 페이지번호, numOfRows: 한 페이지 결과 수
    @Override
    public Map<String, Object> dataDmstMrgBrkAPI(int pageNo, int numOfRows, String ctpvNm) throws UnsupportedEncodingException {
        // 국내결혼중개업 리스트 + API 호출 결과 관련 map
        Map<String, Object> returnApiMap = new HashMap<>();

        // 국내결혼중개업 리스트
        List<DmstMrgBrkDTO> returnDataDmstMrgBrkList = new ArrayList<>();

        // 시도명 인코딩
        String encodedCtpvNm = URLEncoder.encode(ctpvNm, "UTF-8");
//        String encodedCtpvNm = ctpvNm;

        // uribuild 설정을 도와주는 DefaultUriBUilderFactory 호출
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(baseUrl);
        // 인코딩 모드 설정
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);

        try {
            WebClient webClient = WebClient.builder()
                    .uriBuilderFactory(factory)
                    .baseUrl(baseUrl)
                    .build();

            String response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/getDmstMrgBrkpgListV2")
                            .queryParam("serviceKey", serviceKey)
                            .queryParam("pageNo", pageNo)
                            .queryParam("numOfRows", numOfRows)
                            .queryParam("type", "json")
                            .queryParam("ctpvNm", encodedCtpvNm)
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // jackson objectMapper 객체 생성
            ObjectMapper objectMapper = new ObjectMapper();

            // JsonNode 생성
            // readTree() 메소드는 Json 문자열을 받아서 JsonNode 객체를 리턴
            JsonNode jsonNode = objectMapper.readTree(response);
            String jsonStr = jsonNode.findValue("item").toString();

            // API 조회 결과
            List<Map<String, String>> returnResponse = new ObjectMapper().readValue(jsonStr, new TypeReference<List<Map<String, String>>>() {});

            for(int i=0; i<returnResponse.size(); i++) {
                DmstMrgBrkDTO dmstMrgBrkDTO = DmstMrgBrkDTO.builder()
                        .entrpsNm(returnResponse.get(i).get("entrpsNm"))
                        .rprsvNm(returnResponse.get(i).get("rprsvNm"))
                        // 시도명 + 시군구명
                        .addr(returnResponse.get(i).get("ctpvNm") + " " + returnResponse.get(i).get("sggNm"))
                        .operState(returnResponse.get(i).get("operYn"))
                        .crtrYmd(returnResponse.get(i).get("crtrYmd"))
                        .build();

                returnDataDmstMrgBrkList.add(dmstMrgBrkDTO);
            }

            // 국내결혼중개업 운영여부 셋팅
            for(int i=0; i< returnDataDmstMrgBrkList.size(); i++) {
                if(returnDataDmstMrgBrkList.get(i).getOperState().equals("Y"))
                    returnDataDmstMrgBrkList.get(i).setOperState("운영중");
                else
                    returnDataDmstMrgBrkList.get(i).setOperState("미운영");
            }

            // API 호출 성공 시
            DmstMrgBrkApiResultDTO returnDmstMrgBrkApiResultDTO = DmstMrgBrkApiResultDTO.builder()
                    .apiResultCode("a-1")
                    .apiResultMsg("success")
                    .build();

            returnApiMap.put("dataDmstMrgBrkList", returnDataDmstMrgBrkList);
            returnApiMap.put("apiResult", returnDmstMrgBrkApiResultDTO);
        } catch(Exception e) {
            // API 호출 실패 시
            DmstMrgBrkApiResultDTO returnDmstMrgBrkApiResultDTO = DmstMrgBrkApiResultDTO.builder()
                    .apiResultCode("a-0")
                    .apiResultMsg("[ERROR]: 국내결혼중개업 조회 시, 문제가 발생하였습니다. 관리자에게 문의하시기 바랍니다.")
                    .build();

            System.out.println(returnDmstMrgBrkApiResultDTO.getApiResultMsg());

            returnApiMap.put("dataDmstMrgBrkList", null);
            returnApiMap.put("apiResult", returnDmstMrgBrkApiResultDTO);

            e.printStackTrace();
        }

        return returnApiMap;
    }
}
