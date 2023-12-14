package com.saeahga.community.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor // 파라미터가 없는 기본 생성자 생성
@AllArgsConstructor // 모든 필드 값을 파라미터로 받는 생성자 생성
@Builder
public class UserDTO {
    private String userId;
    private String userPw;
    private String userNm;
    private String userPhone;
    private String userEmail;
    private String userAddr;
    private String userType;
    private String userRgstDate;
    private String userModfDate;
    private String userUseYn;
}
