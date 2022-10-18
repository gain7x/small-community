package com.practice.smallcommunity.application;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.practice.smallcommunity.application.auth.MailVerificationService;
import com.practice.smallcommunity.application.exception.BusinessException;
import com.practice.smallcommunity.application.exception.ErrorCode;
import com.practice.smallcommunity.domain.auth.MailVerification;
import com.practice.smallcommunity.domain.auth.MailVerificationRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MailVerificationServiceTest {

    @Mock
    MailVerificationRepository mailVerificationRepository;

    @Mock
    MailService mailService;

    MailVerificationService verificationNumberService;

    @BeforeEach
    void setUp() {
        verificationNumberService = new MailVerificationService(mailVerificationRepository, mailService);
    }

    @Test
    void 인증_메일을_전송한다() {
        //given
        when(mailVerificationRepository.save(any()))
            .thenAnswer(AdditionalAnswers.returnsFirstArg());

        String mail = "test@mail.com";

        //when
        verificationNumberService.sendVerificationMail(mail);

        //then
        verify(mailService, times(1)).send(eq(mail), anyString(), anyString(), any());
    }

    @Test
    void 인증_키를_확인하고_유효하면_인증정보를_반환한다() {
        //given
        String key = "key";
        String mail = "test@mail.com";

        when(mailVerificationRepository.findById(key))
            .thenReturn(Optional.of(MailVerification.builder()
                .key(key)
                .email(mail)
                .build()));

        //when
        //then
        assertThatNoException().isThrownBy(() -> verificationNumberService.check(key));
    }

    @Test
    void 인증번호_검증_시_키가_조회되지_않으면_예외를_던진다() {
        //when
        //then
        assertThatThrownBy(() -> verificationNumberService.check(""))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_VERIFICATION_KEY);
    }
}