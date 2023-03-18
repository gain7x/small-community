package com.practice.smallcommunity.infrastructure;

import com.practice.smallcommunity.common.exception.BusinessException;
import com.practice.smallcommunity.common.exception.ErrorCode;
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

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