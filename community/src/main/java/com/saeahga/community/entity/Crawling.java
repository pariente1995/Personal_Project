package com.saeahga.community.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="T_SAG_CRW_NEWS")
@SequenceGenerator(
        name="NewsSequenceGenerator",
        sequenceName="T_SAG_CRW_NEWS_SEQ",
        initialValue=1, // 시작값
        allocationSize=1 // 메모리를 통해 할당할 범위 사이즈
)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class Crawling {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "NewsSequenceGenerator"
    )
    private int newsNo;
    private String newsTitle;
    private String newsDsc;
    private String newsLink;
    private String newsMedia;
    private String newsImgPath;
    private LocalDateTime newsDate = LocalDateTime.now();
    private LocalDateTime newsCrwDate = LocalDateTime.now();
}
