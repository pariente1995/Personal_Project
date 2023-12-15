package com.saeahga.community.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import java.time.LocalDateTime;

@Entity
@Table(name="T_SAG_USER")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicInsert // UPDATE 구문 생성 시점에 null 이 아닌 컬럼만(변경된 값만) 포함한다.
@Setter
@Getter
public class User {
    @Id
    private String userId;
    private String userPw;
    private String userNm;
    private String userPhone;
    private String userEmail;
    private String userAddr;
    private String userType;
    private LocalDateTime userRgstDate = LocalDateTime.now();
    private LocalDateTime userModfDate = LocalDateTime.now();
    private String userUseYn;
}
