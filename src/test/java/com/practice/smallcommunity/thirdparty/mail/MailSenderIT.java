package com.practice.smallcommunity.thirdparty.mail;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(value = MailSenderAutoConfiguration.class, initializers = ConfigDataApplicationContextInitializer.class)
public class MailSenderIT {

    @Autowired
    JavaMailSender sender;

    @Value("${email.test-target}")
    String testTargetEmail;

    @Disabled
    @Test
    void 메일_전송() throws MessagingException {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
        helper.setTo(testTargetEmail);
        helper.setSubject("Mail sender test title");
        helper.setText("Mail sender test content");

        sender.send(message);
    }
}
