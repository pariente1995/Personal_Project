package com.saeahga.community.config;

import com.saeahga.community.handler.LoginFailureHandler;
import com.saeahga.community.oauth.Oauth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // 클래스가 설정파일이라고 선언
@EnableWebSecurity // Security 의 filterchain 을 구현하기 위한 어노테이션
@RequiredArgsConstructor
public class SecurityConfig {
    /*
    === 스프링 시큐리티 디버그 관련 ===
    // application.properties 에서 읽어온다.
    // "spring.security.debug" 관련 설정이 없을 시, false 가 기본값이 된다.
    @Value("${spring.security.debug:false}")
    boolean securityDebug;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.debug(securityDebug);
    }
    */

    private final LoginFailureHandler loginFailureHandler;
    private final Oauth2UserService oauth2UserService;

    // 비밀번호 암호화를 위한 PasswordEncoder
    // security 에 의해 로그인 처리될 때 비밀번호 비교 시 무조건 사용
    // 복호화는 불가능, match(사용자 입력값(그냥 String 값), DB에 저장된 암호화된 비밀번호) => true 나 false 로 리턴
    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 필터 체인 구현(HttpSecurity 객체 사용)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 권한에 따른 요청주소 매핑
        // "/home"으로 시작하는 요청 리소스는 모든 사람에게 허용
        /*
            1. Spring Security 5.8 이상의 버전을 사용하는 경우에는,
            antMatchers, mvcMatchers, regexMatchers 가 더이상 사용되지 않기 때문에, requestMatchers 를 사용해야 한다.

            2. Spring Security 6.1.0부터는 메서드 체이닝의 사용을 지양하고 람다식을 통해 함수형으로 설정하는 것을 지향한다.
        */
        http.
                authorizeHttpRequests( authorizeHttpRequests ->
                        authorizeHttpRequests
                                // "/home/main" 요청 리소스는 모든 사람에게 허용
                                // 현재 "http://localhost:8080/"로 요청 시, /home/main 으로 들어오게 되어 있음
                                .requestMatchers("/").permitAll()
                                .requestMatchers("/home/main").permitAll()
                                // css, js, images, favicon 같은 정적 리소스들도 권한처리 필수
                                .requestMatchers("/css/**").permitAll()
                                .requestMatchers("/js/**").permitAll()
                                .requestMatchers("/images/**").permitAll()
                                .requestMatchers("/favicon.ico").permitAll()
                                // 누구든 접근 가능하게 함.
                                .requestMatchers("/user/**").permitAll()
                                .requestMatchers("/news/**").permitAll()
                                .requestMatchers("/brokerage/**").permitAll()
                                .requestMatchers("/cafe/**").permitAll()
                                // 권한을 가지고 있는 유저들만 접근할 수 있는 요청리소스 설정
                                // Authentication 객체를 만든 후에 가져올 수 있는 권한들
                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                // 위에 설정하는 요청리소스 제외한 나머지 요청리소스는 인증된 사용자만 접근 가능
                                .anyRequest().authenticated()
                );

        // 로그인, 로그아웃 설정
        // AuthenticationProvider(인증공급자)에게 사용자가 입력한 정보로 만든
        // 인증용 객체 UsernamePasswordAuthenticationToken 을 전달한 상태까지 설정
        http.
                formLogin(formLogin ->
                        formLogin
                                .loginPage("/user/login")
                                // Spring Security 에서는 id는 username
                                // 비밀번호는 password
                                // 다른 키 값으로 사용하고 싶으면 username, password 키 값과 매핑하여 사용한다
                                .usernameParameter("userId")
                                .passwordParameter("userPw")
                                // 로그인 요청이 오면 시큐리티에서 낚아채서 처리
                                // 낚아챌 로그인 요청 url 지정, login-form 으로 이동
                                .loginProcessingUrl("/user/loginProc")
                                // 로그인 성공 후 띄워 줄 화면 url
                                .defaultSuccessUrl("/home/main")
                                .failureHandler(loginFailureHandler)
                );

        http.
                oauth2Login(oauth2Login ->
                        oauth2Login
                                .loginPage("/user/login")
                                // 토큰 발행 후 처리
                                // 토큰이 발행되면 사용자 정보를 받아서 처리 가능해지는 데
                                // 사용자 정보를 웹 사이트에 맞도록 변경해주는 작업 필요
                                .userInfoEndpoint(userInfoEndpoint -> // 사용자 정보를 다 가지고 왔을 때
                                        // 사용자 정보를 웹 사이트에 맞도록 변해주는 service 클래스 등록
                                        userInfoEndpoint.userService(oauth2UserService)
                                )
                );

        http.
                logout(logout ->
                        logout
                                .logoutUrl("/user/logout")
                                .invalidateHttpSession(true)
                                .logoutSuccessUrl("/user/login")
                );


        // 크로스도메인 이슈 방지 설정
        // 크로스도메인: 웹 생태계는 원래 원칙적으로 하나의 웹 어플리케이션에서 요청에 대한 주소가 변경이 되면 안된다.
        // localhost:3000(리액트 화면단 어플리케이션) 다른 도메인으로 요청 -> localhost:9090(스프링부트 API)
        // 스프링부트(백앤드) 어플리케이션에서 크로스도메인(해당 도메인을 지정하여)을 허용해주는 식으로 사용
        // CorsRegistry() 객체를 사용하여 해당 도메인을 크로스도메인 허용
        // CorsRegistry.addMapping("허용될 요청 url")
        // 			   .allowedOrigins("허용될 해당 도메인 주소( ex) http://localhost:3000")
        //			   .allowedMethods("허용될 요청 메소드 타입("GET", "POST" ...)")
        //			   타임아웃시간, 인증에 대한 요청, 요청 헤더를 허용여부 등을 설정
        // http.csrf().disable(): 크로스 도메인 방지
        http
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

}
