package com.saeahga.community.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.sql.DataSource;

@Configuration // eclipse가 설정파일이다 라고 알려줌
@PropertySource("classpath:/application.properties")
public class DBConfig {
    // DB 연동

    @Autowired
    // 스프링 컨테이너 호출: 현재 프로젝트의 설정파일 등을 가져올 수 있다.
    ApplicationContext applicationContext;

    @Bean // 거의 대부분 생성자 메소드 위에 선언
    @ConfigurationProperties(prefix="spring.datasource.hikari")
    // application.properties 파일의 어떤 내용을 읽어서 설정할지 
    public HikariConfig hikariConfig() {
        return new HikariConfig();
    }

    @Bean
    public DataSource dataSource() {
        return new HikariDataSource(hikariConfig());
    }
}
