package com.saeahga.community.handler;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.io.UnsupportedEncodingException;

public class MailHandler {
    private JavaMailSender javaMailSender;
    private MimeMessage message;
    private MimeMessageHelper messageHelper;

    public MailHandler(JavaMailSender sender) throws MessagingException {
        this.javaMailSender = sender;
        this.message = this.javaMailSender.createMimeMessage();
        this.messageHelper = new MimeMessageHelper(message, true, "UTF-8");
    }

    // 메일 발신자
    public void setFrom(String mail, String name) throws UnsupportedEncodingException, MessagingException {
        messageHelper.setFrom(mail, name);
    }

    // 메일 수신자
    public void setTo(String mail) throws MessagingException {
        messageHelper.setTo(mail);
    }

    // 메일 제목
    public void setSubject(String subject) throws MessagingException {
        messageHelper.setSubject(subject);
    }

    // 메일 본문 내용, HTML 여부
    public void setText(String text) throws MessagingException {
        messageHelper.setText(text, true);
    }

    public void send() {
        try {
            // 메일 전송
            javaMailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
