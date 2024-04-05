package com.saeahga.community.controller;

import com.saeahga.community.service.api.ApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/brokerage")
// @RequiredArgsConstructor 는 초기화 되지않은 final 필드나, @NonNull 이 붙은 필드에 대해 생성자를 생성해준다.
@RequiredArgsConstructor
public class DmstMrgBrkController {
    private final ApiService apiService;

    // 국내 결혼 중개업 조회

}
