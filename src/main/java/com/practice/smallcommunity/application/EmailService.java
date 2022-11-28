package com.practice.smallcommunity.application;

import com.practice.smallcommunity.application.exception.BusinessException;
import com.practice.smallcommunity.application.exception.ErrorCode;
import java.util.HashMap;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@RequiredArgsConstructor
@Service
public class EmailService {

    private final JavaMailSender sender;

    private final TemplateEngine templateEngine;

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

    @Async
    public void send(String email, String title, String template, HashMap<String, Object> model) {
        Context context = new Context();
        model.forEach(context::setVariable);

        String htmlContent = templateEngine.process(template, context);
        send(email, title, htmlContent);
    }
}
