package com.saeahga.community.service.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.saeahga.community.dto.DmstMrgBrkDTO;

import java.util.List;

public interface ApiService {
    // 공공데이터포털 -> 국내결혼중개업 API
    // pageNo: 페이지번호, numOfRows: 한 페이지 결과 수
    List<DmstMrgBrkDTO> dataDmstMrgBrkAPI(int pageNo, int numOfRows);
}
