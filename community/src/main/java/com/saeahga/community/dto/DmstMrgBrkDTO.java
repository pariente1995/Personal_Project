package com.saeahga.community.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor // 파라미터가 없는 기본 생성자 생성
@AllArgsConstructor // 모든 필드 값을 파라미터로 받는 생성자 생성
@Builder
public class DmstMrgBrkDTO {
    private String entrpsNm; // 업체명
    private String rprsvNm; // 대표자명
    private String addr; // 주소
    private String operState; // 운영상태
    private String crtrYmd; // 기준일자
}
