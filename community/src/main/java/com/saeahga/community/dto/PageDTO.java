package com.saeahga.community.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor // 파라미터가 없는 기본 생성자 생성
@AllArgsConstructor // 모든 필드 값을 파라미터로 받는 생성자 생성
@Builder
public class PageDTO {
    private int totalElements; // 맘카페 총 리스트 수
    private int pageNumber; // 선택한 페이지 번호
    private int pageRange; // 페이지 리스트 범위, ex) 5 => 1,2,3,4,5 / 6,7,8,9,10 ...
    private int totalPages; // 맘카페 총 페이지 수
}