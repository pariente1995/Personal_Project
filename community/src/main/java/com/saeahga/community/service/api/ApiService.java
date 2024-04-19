package com.saeahga.community.service.api;

import com.saeahga.community.dto.DmstMrgBrkDTO;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public interface ApiService {
    // 공공데이터포털 -> 국내결혼중개업 API
    // pageNo: 페이지번호, numOfRows: 한 페이지 결과 수
    Map<String, Object> dataDmstMrgBrkAPI(DmstMrgBrkDTO dmstMrgBrkDTO, int pageNo, int numOfRows) throws UnsupportedEncodingException;
}
