package com.practice.smallcommunity.infrastructure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.internet.MimeMessage;
import java.util.HashMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TemplateMailSenderTest {

    @Mock
    JavaMailSender javaMailSender;

    TemplateEngine templateEngine;

    TemplateMailSender mailSender;

    String email = "test@mail.com";

    @BeforeEach
    void setUp() {
        templateEngine = spy(new SpringTemplateEngine());
        mailSender = new TemplateMailSender(javaMailSender, templateEngine);

        when(javaMailSender.createMimeMessage())
                .thenReturn(mock(MimeMessage.class));
    }

    @Test
    void 템플릿_엔진을_활용하여_이메일을_전송한다() {
        //when
        mailSender.send(email, "title", "templates", new HashMap<>());

        //then
        verify(javaMailSender, times(1)).send(any(MimeMessage.class));
    }
}