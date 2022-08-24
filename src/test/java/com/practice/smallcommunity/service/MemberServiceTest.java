package com.practice.smallcommunity.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.domain.member.MemberRole;
import com.practice.smallcommunity.exception.ValidationErrorException;
import com.practice.smallcommunity.domain.member.MemberRepository;
import com.practice.smallcommunity.service.member.MemberService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    MemberRepository memberRepository;

    MemberService memberService;

    @BeforeEach()
    void beforeEach() {
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        memberService = new MemberService(memberRepository, passwordEncoder);
    }

    Member targetMember = Member.builder()
        .id(1L)
        .email("userA@mail.com")
        .password("pass")
        .nickname("firstUser")
        .build();

    @Test
    void 회원가입() {
        //given
        when(memberRepository.save(any(Member.class)))
            .thenAnswer(AdditionalAnswers.returnsFirstArg());

        //when
        Member registeredMember = memberService.registerMember(targetMember);

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
        Member registeredMember = memberService.registerMember(targetMember);

        //then
        assertThat(registeredMember).isNotNull();
        assertThat(registeredMember.getMemberRole()).isEqualTo(MemberRole.ROLE_USER);
    }

    @Test
    void 회원가입하면_비밀번호는_bcrypt로_암호화된다() {
        //given
        when(memberRepository.save(any(Member.class)))
            .thenAnswer(AdditionalAnswers.returnsFirstArg());

        String plainPassword = targetMember.getPassword();

        //when
        Member registeredMember = memberService.registerMember(targetMember);

        //then
        assertThat(registeredMember).isNotNull();
        assertThat(registeredMember.getPassword()).isNotEqualTo(plainPassword);
        // '{bcrypt}암호화된 문자열' 형식 검증
        assertThat(registeredMember.getPassword()).startsWith("{");
    }

    @Test
    void 회원가입_시_이메일이_중복되면_예외를_던진다() {
        //given
        when(memberRepository.existsByEmail(targetMember.getEmail()))
            .thenReturn(true);

        //when
        //then
        assertThatThrownBy(() -> memberService.registerMember(targetMember))
            .isInstanceOf(ValidationErrorException.class);
    }

    @Test
    void 회원가입_시_별명이_중복되면_예외를_던진다() {
        //given
        when(memberRepository.existsByNickname(targetMember.getNickname()))
            .thenReturn(true);

        //when
        //then
        assertThatThrownBy(() -> memberService.registerMember(targetMember))
            .isInstanceOf(ValidationErrorException.class);
    }

    @Test
    void 회원을_식별자로_조회한다() {
        //given
        when(memberRepository.findById(targetMember.getId()))
            .thenReturn(Optional.of(targetMember));

        //when
        Member findMember = memberService.findByUserId(targetMember.getId());

        //then
        assertThat(findMember.getId()).isEqualTo(targetMember.getId());
    }

    @Test
    void 식별자로_조회_시_동일한_식별자가_없으면_예외를_던진다() {
        //given
        when(memberRepository.findById(targetMember.getId()))
            .thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> memberService.findByUserId(targetMember.getId()))
            .isInstanceOf(IllegalArgumentException.class);
    }
}