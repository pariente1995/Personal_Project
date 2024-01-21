package com.saeahga.community.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;

@Component
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        // 에러 메시지가 한글일 경우, 정상적으로 출력되지 않기에 에러 메시지를 인코딩
        String errorMessage = URLEncoder.encode(getExceptionMessage(exception), "UTF-8");

        // 로그인 실패 시, 요청되는 url 을 지정
        setDefaultFailureUrl("/user/login?error=true&errorMsg=" + errorMessage);

        super.onAuthenticationFailure(request, response, exception);
    }

    // 로그인 실패 경우에 따른 에러 메시지 설정
    private String getExceptionMessage(AuthenticationException exception) {
        if(exception instanceof BadCredentialsException) {
            return "비밀번호가 일치하지 않습니다.";
        } else if(exception instanceof UsernameNotFoundException) {
            return "계정없음";
        } else if(exception instanceof AccountExpiredException) {
            return "계정만료";
        } else if(exception instanceof CredentialsExpiredException) {
            return "비밀번호만료";
        } else if(exception instanceof DisabledException) {
            return "계정비활성화";
        } else if(exception instanceof LockedException) {
            return "계정잠김";
        } else if(exception.getMessage().contains("UserDetailsService returned null")) {
            return "존재하지 않는 계정입니다.";
        } else {
            return "확인되지 않은 에러 발생";
        }
    }
}
