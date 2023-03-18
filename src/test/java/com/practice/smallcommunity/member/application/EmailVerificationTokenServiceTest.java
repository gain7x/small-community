package com.practice.smallcommunity.member.application;

import com.practice.smallcommunity.common.exception.BusinessException;
import com.practice.smallcommunity.common.exception.ErrorCode;
import com.practice.smallcommunity.infrastructure.TemplateMailSender;
import com.practice.smallcommunity.member.domain.EmailVerificationToken;
import com.practice.smallcommunity.member.domain.EmailVerificationTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailVerificationTokenServiceTest {

    @Mock
    EmailVerificationTokenRepository emailVerificationTokenRepository;

    @Mock
    TemplateMailSender mailSender;

    EmailVerificationService verificationNumberService;

    @BeforeEach
    void setUp() {
        verificationNumberService = new EmailVerificationService(emailVerificationTokenRepository, mailSender);
    }

    @Test
    void 인증_메일을_전송한다() {
        //given
        when(emailVerificationTokenRepository.save(any()))
                .thenAnswer(AdditionalAnswers.returnsFirstArg());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("https");
        request.setServerPort(8443);
        request.setRequestURI("/api/v1/members");

        ServletRequestAttributes requestAttributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(requestAttributes);

        String email = "test@mail.com";
        String redirectUri = "https://localhost:3000";

        //when
        verificationNumberService.sendVerificationMail(email, redirectUri);

        //then
        verify(emailVerificationTokenRepository, times(1)).save(any());
        verify(mailSender, times(1)).send(eq(email), anyString(), anyString(), argThat(arg -> {
            String action = arg.get("action").toString();
            return action.startsWith("https://localhost:8443/");
        }));
    }

    @Test
    void 인증_정보를_확인하고_유효하면_인증정보를_반환한다() {
        //given
        String email = "test@mail.com";
        String key = "key";

        when(emailVerificationTokenRepository.findById(email))
            .thenReturn(Optional.of(EmailVerificationToken.builder()
                .email(email)
                .key(key)
                .build()));

        //when
        //then
        assertThatNoException().isThrownBy(() -> verificationNumberService.check(email, key));
    }

    @Test
    void 인증정보_검증_시_이메일이_조회되지_않으면_예외를_던진다() {
        //when
        //then
        assertThatThrownBy(() -> verificationNumberService.check("", ""))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_VERIFICATION_DATA);
    }

    @Test
    void 인증정보_검증_시_인증_키가_일치하지_않으면_예외를_던진다() {
        //given
        String email = "test@mail.com";
        String key = "key";

        when(emailVerificationTokenRepository.findById(email))
            .thenReturn(Optional.of(EmailVerificationToken.builder()
                .email(email)
                .key(key)
                .build()));

        //when
        //then
        assertThatThrownBy(() -> verificationNumberService.check(email, ""))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_VERIFICATION_DATA);
    }
}