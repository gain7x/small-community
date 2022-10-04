package com.practice.smallcommunity.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import com.practice.smallcommunity.application.exception.BusinessException;
import com.practice.smallcommunity.application.exception.ErrorCode;
import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.domain.member.MemberRepository;
import com.practice.smallcommunity.domain.member.MemberRole;
import com.practice.smallcommunity.utils.DomainGenerator;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    MemberRepository memberRepository;

    MemberService memberService;

    @Spy
    Member targetMember = DomainGenerator.createMember("A");

    @BeforeEach()
    void beforeEach() {
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        memberService = new MemberService(memberRepository, passwordEncoder);
    }

    @Test
    void 회원가입() {
        //given
        when(memberRepository.save(any(Member.class)))
            .thenAnswer(AdditionalAnswers.returnsFirstArg());

        //when
        Member registeredMember = memberService.register(targetMember);

        //then
        assertThat(registeredMember).isNotNull();
        assertThat(registeredMember.getEmail()).isEqualTo(targetMember.getEmail());
    }

    @Test
    void 등록되는_회원은_사용자_권한을_가진다() {
        //given
        when(memberRepository.save(any(Member.class)))
            .thenAnswer(AdditionalAnswers.returnsFirstArg());

        //when
        Member registeredMember = memberService.register(targetMember);

        //then
        assertThat(registeredMember).isNotNull();
        assertThat(registeredMember.getMemberRole()).isEqualTo(MemberRole.USER);
    }

    @Test
    void 회원가입하면_비밀번호는_bcrypt로_암호화된다() {
        //given
        when(memberRepository.save(any(Member.class)))
            .thenAnswer(AdditionalAnswers.returnsFirstArg());

        String plainPassword = targetMember.getPassword();

        //when
        Member registeredMember = memberService.register(targetMember);

        //then
        assertThat(registeredMember).isNotNull();
        assertThat(registeredMember.getPassword()).isNotEqualTo(plainPassword);
        // '{bcrypt}암호화된 문자열' 형식 검증
        assertThat(registeredMember.getPassword()).startsWith("{bcrypt}");
    }

    @Test
    void 회원가입_시_이메일이_중복되면_예외를_던진다() {
        //given
        when(memberRepository.existsByEmail(targetMember.getEmail()))
            .thenReturn(true);

        //when
        //then
        assertThatThrownBy(() -> memberService.register(targetMember))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATED_EMAIL);
    }

    @Test
    void 회원가입_시_별명이_중복되면_예외를_던진다() {
        //given
        when(memberRepository.existsByNickname(targetMember.getNickname()))
            .thenReturn(true);

        //when
        //then
        assertThatThrownBy(() -> memberService.register(targetMember))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATED_NICKNAME);
    }

    @Test
    void 회원을_ID로_조회한다() {
        //given
        when(memberRepository.findByIdAndWithdrawalIsFalse(targetMember.getId()))
            .thenReturn(Optional.of(targetMember));

        //when
        Member findMember = memberService.findByUserId(targetMember.getId());

        //then
        assertThat(findMember.getId()).isEqualTo(targetMember.getId());
    }

    @Test
    void 회원을_ID로_조회_시_일치하는_회원이_없으면_예외를_던진다() {
        //given
        when(memberRepository.findByIdAndWithdrawalIsFalse(1L))
            .thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> memberService.findByUserId(1L))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_FOUND_MEMBER);
    }

    @Test
    void 회원을_이메일로_조회한다() {
        //given
        when(memberRepository.findByEmailAndWithdrawalIsFalse(targetMember.getEmail()))
            .thenReturn(Optional.of(targetMember));

        //when
        Member findMember = memberService.findByEmail(targetMember.getEmail());

        //then
        assertThat(findMember.getId()).isEqualTo(targetMember.getId());
    }

    @Test
    void 회원을_이메일로_조회_시_일치하는_회원이_없으면_예외를_던진다() {
        //given
        when(memberRepository.findByEmailAndWithdrawalIsFalse(targetMember.getEmail()))
            .thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> memberService.findByEmail(targetMember.getEmail()))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_FOUND_MEMBER);
    }

    @Test
    void 회원정보를_변경한다() {
        //given
        String expected = "새로운 별명";

        when(memberRepository.findByIdAndWithdrawalIsFalse(1L))
            .thenReturn(Optional.of(targetMember));

        //when
        Member result = memberService.update(1L, expected);

        //then
        assertThat(result.getNickname()).isEqualTo(expected);
    }

    @Test
    void 회원정보_변경_시_별명이_중복되면_예외를_던진다() {
        //given
        String newNickname = "새로운 별명";

        when(memberRepository.findByIdAndWithdrawalIsFalse(1L))
            .thenReturn(Optional.of(targetMember));

        when(memberRepository.existsByNickname(newNickname))
            .thenReturn(true);

        //when
        //then
        assertThatThrownBy(() -> memberService.update(1L, newNickname))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATED_NICKNAME);
    }
}