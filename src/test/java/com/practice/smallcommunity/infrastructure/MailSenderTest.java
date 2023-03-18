package com.practice.smallcommunity.infrastructure;

import com.practice.smallcommunity.common.exception.BusinessException;
import com.practice.smallcommunity.common.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.internet.MimeMessage;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MailSenderTest {

    @Mock
    JavaMailSender javaMailSender;

    MailSender mailSender;

    String email = "test@mail.com";

    @BeforeEach
    void setUp() {
        mailSender = new MailSender(javaMailSender);

        when(javaMailSender.createMimeMessage())
            .thenReturn(mock(MimeMessage.class));
    }

    @Test
    void 메일을_전송한다() {
        //when
        doNothing().when(javaMailSender).send(any(MimeMessage.class));

        mailSender.send(email, "title", "htmlContent");

        //then
        verify(javaMailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void 메일_전송_시_오류가_발생하면_비즈니스_예외를_던진다() {
        //when
        //then
        assertThatThrownBy(() -> mailSender.send("invalid\tEmail", "title", "htmlContent"))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.RUNTIME_ERROR);
    }
}