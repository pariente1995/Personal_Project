package com.saeahga.community.service.mail.impl;

import com.saeahga.community.handler.MailHandler;
import com.saeahga.community.service.mail.MailService;
import lombok.RequiredArgsConstructor;
import org.apache.groovy.util.Maps;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
// @RequiredArgsConstructor 는 초기화 되지않은 final 필드나, @NonNull 이 붙은 필드에 대해 생성자를 생성해준다.
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {
    private final JavaMailSender javaMailSender;

    @Override
    public Map<String, Object> send(String email, String title, String body) {
        MailHandler mail;

        try {
            mail = new MailHandler(javaMailSender);
//            mail.setFrom("발신자 메일", "발신자 이름"); // 발신자
            mail.setFrom("no-reply@saeahga.com", "SAEAHGA"); // 발신자
            mail.setTo(email); // 수신자
            mail.setSubject(title); // 제목
            mail.setText(body); // 내용
            mail.send();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String resultCode = "S-1";
        String msg = "메일이 발송되었습니다.";

        return Maps.of("resultCode", resultCode, "msg", msg);
    }
}
