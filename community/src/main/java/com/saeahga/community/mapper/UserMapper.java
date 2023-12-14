package com.saeahga.community.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

// DAO 역할을 해서 DAO 파일 만들 필요 없음
// user-mapper.xml로 연결해서 DB로 들어갔다가 값들을 받아서 나옴.
@Mapper
public interface UserMapper {
    // 예시
    // 카테고리 선택에 따른 데이트 코스 리스트 조회_세혁
    // List<CamelHashMap> getPageCateDatecourseList(Map<String, Object> paramMap);

    // 테스트
    // 사용자 조회
    public Map<String, Object> getUser(String userId);

}
