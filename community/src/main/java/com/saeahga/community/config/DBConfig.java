package com.saeahga.community.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration // 설정파일이다 라고 알려줌
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

    // Mybatis 연동 START ======
    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setConfigLocation(
                applicationContext.getResource("classpath:mybatis-config.xml"));
        sqlSessionFactoryBean.setMapperLocations(
                applicationContext.getResources("classpath:mapper/**/*-mapper.xml")); // mapper의 경우 여러개이기에 getResources로 쓸 것!

        return sqlSessionFactoryBean.getObject();
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
    // Mybatis 연동 END ======

    // JPA 연동 START ======
    @Bean
    @ConfigurationProperties(prefix="spring.jpa")
    public Properties hibernateConfig() {
        return new Properties();
    }
    // JPA 연동 END ======
}
