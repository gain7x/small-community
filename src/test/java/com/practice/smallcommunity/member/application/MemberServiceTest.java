package com.practice.smallcommunity.member.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.practice.smallcommunity.common.exception.BusinessException;
import com.practice.smallcommunity.common.exception.ErrorCode;
import com.practice.smallcommunity.member.domain.Member;
import com.practice.smallcommunity.member.domain.MemberRepository;
import com.practice.smallcommunity.testutils.DomainGenerator;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    MemberRepository memberRepository;

    MemberService memberService;

    @Spy
    Member targetMember = DomainGenerator.createMember("A");

    @BeforeEach()
    void beforeEach() {
        memberService = new MemberService(memberRepository);
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

    @Test
    void 회원을_탈퇴_상태로_변경한다() {
        //given
        when(memberRepository.findByIdAndWithdrawalIsFalse(1L))
            .thenReturn(Optional.of(targetMember));

        //when
        Member result = memberService.withdrawal(1L);

        //then
        assertThat(result.isWithdrawal()).isTrue();
    }

    @Test
    void 이미_사용중인_이메일이면_예외를_던진다() {
        //given
        when(memberRepository.existsByEmail("test@mail.com"))
            .thenReturn(true);

        //when
        //then
        assertThatThrownBy(() -> memberService.checkDuplicateEmails("test@mail.com"))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATED_EMAIL);
    }

    @Test
    void 이미_사용중인_별명이면_예외를_던진다() {
        //given
        when(memberRepository.existsByNickname("test"))
            .thenReturn(true);

        //when
        //then
        assertThatThrownBy(() -> memberService.checkDuplicateNicknames("test"))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATED_NICKNAME);
    }

    @Test
    void 가입하는_회원정보의_유효성을_확인한다() {
        //given
        when(memberRepository.existsByEmail(targetMember.getEmail()))
            .thenReturn(false);
        when(memberRepository.existsByNickname(targetMember.getNickname()))
            .thenReturn(false);

        //when
        //then
        assertThatNoException().isThrownBy(() -> memberService.validateRegistration(targetMember));
    }

    @Test
    void 가입하는_회원정보가_유효하지_않은_경우_예외를_던진다() {
        //given
        when(memberRepository.existsByEmail(targetMember.getEmail()))
            .thenReturn(true);

        //when
        //then
        assertThatThrownBy(() -> memberService.validateRegistration(targetMember))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATED_EMAIL);
    }
}