package com.practice.smallcommunity.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import com.practice.smallcommunity.common.exception.BusinessException;
import com.practice.smallcommunity.common.exception.ErrorCode;
import com.practice.smallcommunity.member.application.MemberService;
import com.practice.smallcommunity.auth.domain.Login;
import com.practice.smallcommunity.auth.domain.LoginRepository;
import com.practice.smallcommunity.member.domain.Member;
import com.practice.smallcommunity.testutils.DomainGenerator;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock
    LoginRepository loginRepository;

    @Mock
    MemberService memberService;

    PasswordEncoder passwordEncoder;

    LoginService loginService;

    Member member = spy(DomainGenerator.createMember("A"));
    Login login = DomainGenerator.createLogin(member);

    @BeforeEach()
    void beforeEach() {
        passwordEncoder = spy(PasswordEncoderFactories.createDelegatingPasswordEncoder());
        loginService = new LoginService(loginRepository, memberService, passwordEncoder);
    }

    @Test
    void 엔티티를_ID_기준으로_조회한다() {
        //given
        when(loginRepository.findById(1L))
            .thenReturn(Optional.of(Login.builder()
                .build()));

        //when
        assertThatNoException().isThrownBy(() -> loginService.findById(1L));
    }

    @Test
    void 아이디와_비밀번호가_일치하는_로그인_정보를_반환한다() {
        //given
        String email = login.getMember().getEmail();
        login.verifyEmail();
        when(loginRepository.findByMemberIdFetchJoin(any())).thenReturn(Optional.of(login));
        when(memberService.findByEmail(email)).thenReturn(member);
        doReturn(true).when(passwordEncoder).matches(anyString(), anyString());

        //when
        //then
        assertThatNoException().isThrownBy(() -> loginService.login(email, "testPassword"));
    }

    @Test
    void 로그인_시_회원정보가_없으면_예외를_던진다() {
        //given
        when(memberService.findByEmail("test@mail.com"))
            .thenThrow(new BusinessException(ErrorCode.NOT_FOUND_MEMBER));

        //when
        //then
        assertThatThrownBy(() -> loginService.login("test@mail.com", "testPassword"))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_FOUND_MEMBER);
    }

    @Test
    void 로그인_시_이메일이_미인증_상태면_예외를_던진다() {
        //given
        when(memberService.findByEmail(member.getEmail())).thenReturn(member);
        when(loginRepository.findByMemberIdFetchJoin(any())).thenReturn(Optional.of(login));

        //when
        //then
        assertThatThrownBy(() -> loginService.login(member.getEmail(), "testPassword"))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.UNVERIFIED_EMAIL);
    }

    @Test
    void 로그인_시_비밀번호가_일치하지_않으면_예외를_던진다() {
        //given
        doReturn(false).when(passwordEncoder).matches(anyString(), anyString());
        when(memberService.findByEmail(member.getEmail())).thenReturn(member);
        when(loginRepository.findByMemberIdFetchJoin(any())).thenReturn(Optional.of(login));
        login.verifyEmail();

        //when
        //then
        assertThatThrownBy(() -> loginService.login(member.getEmail(), "testPassword"))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_MATCH_MEMBER);
    }

    @Test
    void 회원가입() {
        //given
        when(loginRepository.save(any(Login.class))).thenAnswer(AdditionalAnswers.returnsFirstArg());

        //when
        Login registeredLogin = loginService.register(login);

        //then
        assertThat(registeredLogin).isNotNull();
        assertThat(registeredLogin.getMember()).isEqualTo(login.getMember());
    }

    @Test
    void 회원가입하면_비밀번호는_bcrypt로_암호화된다() {
        //given
        when(loginRepository.save(any(Login.class))).thenAnswer(AdditionalAnswers.returnsFirstArg());

        String plainPassword = login.getPassword();

        //when
        Login registeredLogin = loginService.register(login);

        //then
        assertThat(registeredLogin).isNotNull();
        assertThat(registeredLogin.getPassword()).isNotEqualTo(plainPassword);
        // '{bcrypt}암호화된 문자열' 형식 검증
        assertThat(registeredLogin.getPassword()).startsWith("{bcrypt}");
    }

    @Test
    void 회원가입_시_이메일이_중복되면_예외를_던진다() {
        //given
        doThrow(new BusinessException(ErrorCode.DUPLICATED_EMAIL))
            .when(memberService).validateRegistration(member);

        //when
        //then
        assertThatThrownBy(() -> loginService.register(login))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATED_EMAIL);
    }

    @Test
    void 회원가입_시_별명이_중복되면_예외를_던진다() {
        //given
        doThrow(new BusinessException(ErrorCode.DUPLICATED_NICKNAME))
            .when(memberService).validateRegistration(member);

        //when
        //then
        assertThatThrownBy(() -> loginService.register(login))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATED_NICKNAME);
    }

    @Test
    void 비밀번호를_변경한다() {
        //given
        String currentEncodedPassword = "{noop}testPassword";
        login.changePassword(currentEncodedPassword);
        when(loginRepository.findByMemberIdFetchJoin(1L)).thenReturn(Optional.of(login));

        //when
        Login result = loginService.changePassword(1L, "testPassword", "newPassword");

        //then
        assertThat(result.getPassword()).isNotEqualTo(currentEncodedPassword);
    }

    @Test
    void 비밀번호_변경_시_이전_비밀번호가_일치하지_않으면_예외를_던진다() {
        //given
        login.changePassword("{noop}testPassword");
        when(loginRepository.findByMemberIdFetchJoin(1L)).thenReturn(Optional.of(login));

        //when
        //then
        assertThatThrownBy(() -> loginService.changePassword(1L, "test", "new"))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_MATCH_MEMBER);
    }

    @Test
    void 이메일을_인증_상태로_변경한다() {
        //given
        when(memberService.findByEmail(member.getEmail())).thenReturn(member);
        when(loginRepository.findByMemberIdFetchJoin(any())).thenReturn(Optional.of(login));

        //when
        Login result = loginService.verifyEmail(member.getEmail());

        //then
        assertThat(result.isEmailVerified()).isTrue();
    }

    @Test
    void 미인증_회원정보를_삭제한다() {
        //given
        when(memberService.findByEmail(member.getEmail())).thenReturn(member);
        when(member.getId()).thenReturn(1L);
        when(loginRepository.findByMemberIdFetchJoin(1L)).thenReturn(Optional.of(login));

        //when
        boolean result = loginService.deleteEmailIfNotVerified(member.getEmail());

        //then
        assertThat(result).isTrue();
    }

    @Test
    void 미인증_회원정보_삭제_시_인증된_회원이면_false를_반환한다() {
        //given
        when(memberService.findByEmail(member.getEmail())).thenReturn(member);
        when(member.getId()).thenReturn(1L);
        when(loginRepository.findByMemberIdFetchJoin(1L)).thenReturn(Optional.of(login));
        login.verifyEmail();

        //when
        boolean result = loginService.deleteEmailIfNotVerified(member.getEmail());

        //then
        assertThat(result).isFalse();
    }

    @Test
    void 미인증_회원정보_삭제_시_조회되는_정보가_없으면_false를_반환한다() {
        //given
        when(memberService.findByEmail(member.getEmail()))
            .thenThrow(new BusinessException(ErrorCode.NOT_FOUND_MEMBER));

        //when
        boolean result = loginService.deleteEmailIfNotVerified(member.getEmail());

        //then
        assertThat(result).isFalse();
    }
}