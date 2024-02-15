package com.saeahga.community.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="T_SAG_USER")
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
