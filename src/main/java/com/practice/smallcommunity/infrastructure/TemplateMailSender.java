package com.practice.smallcommunity.infrastructure;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Component
public class TemplateMailSender extends MailSender {

    private final TemplateEngine templateEngine;

    public TemplateMailSender(JavaMailSender sender, TemplateEngine templateEngine) {
        super(sender);
        this.templateEngine = templateEngine;
    }

    @Async
    public void send(String email, String title, String template, Map<String, Object> model) {
        String htmlContent = templateEngine.process(template, makeContext(model));
        send(email, title, htmlContent);
    }

    private Context makeContext(Map<String, Object> model) {
        Context context = new Context();
        model.forEach(context::setVariable);
        return context;
    }
}
