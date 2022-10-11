package com.practice.smallcommunity.application;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {

    @Mock
    JavaMailSender javaMailSender;

    @Mock
    TemplateEngine templateEngine;

    MailService verificationMailService;

    String email = "test@mail.com";

    @BeforeEach
    void setUp() {
        verificationMailService = new MailService(javaMailSender, templateEngine);

        when(javaMailSender.createMimeMessage())
            .thenReturn(mock(MimeMessage.class));

        doNothing()
            .when(javaMailSender).send(any(MimeMessage.class));
    }

    @Test
    void 메일을_전송한다() {
        //when
        verificationMailService.send(email, "title", "htmlContent");

        //then
        verify(javaMailSender, times(1)).send(any(MimeMessage.class));
    }
}