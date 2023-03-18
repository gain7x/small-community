package com.practice.smallcommunity.infrastructure;

import com.practice.smallcommunity.common.exception.BusinessException;
import com.practice.smallcommunity.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@RequiredArgsConstructor
public class MailSender {

    private final JavaMailSender sender;

    @Async
    public void send(String email, String title, String htmlContent) {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

        try {
            helper.setTo(email);
            helper.setSubject(title);
            helper.setText(htmlContent, true);
        } catch (MessagingException e) {
            throw new BusinessException(ErrorCode.RUNTIME_ERROR, null, e);
        }

        sender.send(message);
    }
}
