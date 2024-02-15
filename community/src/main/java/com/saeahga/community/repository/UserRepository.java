package com.saeahga.community.repository;

import com.saeahga.community.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User, String> { // JpaRepository<엔티티, 키 값>
    // 이름으로 사용자 수 조회
    @Query(value="SELECT COUNT(*) FROM T_SAG_USER WHERE USER_NM = :userNm", nativeQuery=true)
    int getUserNmCnt(@Param("userNm") String userNm);

    // 아이디 찾기(이름과 이메일로 사용자 조회)
    // UserServiceImpl 에서 넘겨주는 파라미터의 변수명이 받아주는 변수의 이름과 다를 때, 해당 파라미터이름을 명시
    // 매퍼나 리포지토리에 여러 개의 파라미터를 보낼 때 @Param 어노테이션 사용!
    List<User> findByUserNmAndUserEmail(@Param("userNm") String userNm, @Param("userEmail") String userEmail);

    // 비밀번호 찾기(아이디와 이메일로 사용자 조회)
    Optional<User> findByUserIdAndUserEmail(@Param("userId") String userId, @Param("userEmail") String userEmail);

    // 새 비밀번호 업데이트
    @Modifying
    @Query(value="UPDATE T_SAG_USER"
             + "	 SET USER_PW = :#{#user.userPw}"
             + "   WHERE USER_ID = :#{#user.userId}", nativeQuery=true)
    void updatePw(@Param("user") User user);

    // 회원정보 수정
    @Modifying
    @Query(value="UPDATE T_SAG_USER"
            + "      SET  USER_PW = :#{#user.userPw}"
            + "         , USER_NM = :#{#user.userNm}"
            + "         , USER_PHONE = :#{#user.userPhone}"
            + "         , USER_EMAIL = :#{#user.userEmail}"
            + "         , USER_ADDR = :#{#user.userAddr}"
            + "         , USER_MODF_DATE = :#{#user.userModfDate}"
            + "    WHERE USER_ID = :#{#user.userId}", nativeQuery=true)
    void updateUserInfo(@Param("user") User user);
}
