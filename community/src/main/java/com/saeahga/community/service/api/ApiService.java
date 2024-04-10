package com.saeahga.community.service.api;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

public interface ApiService {
    // 공공데이터포털 -> 국내결혼중개업 API
    // pageNo: 페이지번호, numOfRows: 한 페이지 결과 수, ctpvNm: 시도명
    Map<String, Object> dataDmstMrgBrkAPI(int pageNo, int numOfRows, String ctpvNm) throws UnsupportedEncodingException;
}
