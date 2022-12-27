package com.practice.smallcommunity.application;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.practice.smallcommunity.application.exception.BusinessException;
import com.practice.smallcommunity.application.exception.ErrorCode;
import java.util.HashMap;
import javax.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring5.SpringTemplateEngine;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    JavaMailSender javaMailSender;

    TemplateEngine templateEngine;

    EmailService verificationEmailService;

    String email = "test@mail.com";

    @BeforeEach
    void setUp() {
        templateEngine = spy(new SpringTemplateEngine());
        verificationEmailService = new EmailService(javaMailSender, templateEngine);

        when(javaMailSender.createMimeMessage())
            .thenReturn(mock(MimeMessage.class));
    }

    @Test
    void 메일을_전송한다() {
        //when
        doNothing().when(javaMailSender).send(any(MimeMessage.class));

        verificationEmailService.send(email, "title", "htmlContent");

        //then
        verify(javaMailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void 메일_전송_시_오류가_발생하면_비즈니스_예외를_던진다() {
        //when
        //then
        assertThatThrownBy(() -> verificationEmailService.send("invalid\tEmail", "title", "htmlContent"))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.RUNTIME_ERROR);
    }

    @Test
    void 템플릿_엔진을_활용하여_이메일을_전송한다() {
        //when
        verificationEmailService.send(email, "title", "templates", new HashMap<>());

        //then
        verify(javaMailSender, times(1)).send(any(MimeMessage.class));
    }
}