package com.practice.smallcommunity.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.repository.member.MemberRepository;
import com.practice.smallcommunity.service.member.MemberMapper;
import com.practice.smallcommunity.service.member.MemberMapperImpl;
import com.practice.smallcommunity.service.member.MemberService;
import com.practice.smallcommunity.service.member.dto.MemberRegisterDto;
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

    MemberMapper memberMapper = new MemberMapperImpl();
    PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    MemberService memberService;

    @BeforeEach()
    void beforeEach() {
        memberService = new MemberService(memberRepository, memberMapper, passwordEncoder);
    }

    @Test
    void 회원가입() {
        when(memberRepository.save(any(Member.class)))
            .thenAnswer(AdditionalAnswers.returnsFirstArg());

        MemberRegisterDto registerDto = MemberRegisterDto.builder()
            .username("UserA")
            .password("some-pass")
            .email("some@mail.com")
            .build();

        Member member = memberService.registerMember(registerDto);

        assertThat(member).isNotNull();
        assertThat(member.getUsername()).isEqualTo(registerDto.getUsername());
    }

    @Test
    void 회원가입하면_비밀번호는_bcrypt로_암호화된다() {
        when(memberRepository.save(any(Member.class)))
            .thenAnswer(AdditionalAnswers.returnsFirstArg());

        MemberRegisterDto registerDto = MemberRegisterDto.builder()
            .username("UserA")
            .password("some-pass")
            .email("some@mail.com")
            .build();

        Member member = memberService.registerMember(registerDto);

        assertThat(member).isNotNull();
        assertThat(member.getPassword()).isNotEqualTo(registerDto.getPassword());
        // '{bcrypt}암호화된 문자열' 형식 검증
        assertThat(member.getPassword()).startsWith("{");
    }
}