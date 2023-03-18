package com.practice.smallcommunity.content.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class VoteHistoryIdTest {

    @Test
    void 클래스가_일치하지_않으면_다른_객체이다() {
        //given
        VoteHistoryId voteHistoryId = new VoteHistoryId(1L, 1L);
        Object object = new Object();

        //when
        boolean result = voteHistoryId.equals(object);

        //then
        assertThat(result).isFalse();
    }

    @Test
    void 회원ID와_컨텐츠ID가_같으면_동일한_객체이다() {
        //given
        VoteHistoryId voteHistoryId = new VoteHistoryId(1L, 1L);
        VoteHistoryId voteHistoryId2 = new VoteHistoryId(1L, 1L);

        //when
        boolean result = voteHistoryId.equals(voteHistoryId2);

        //then
        assertThat(result).isTrue();
    }

    @Test
    void equals_메서드가_동일_객체로_판단한_경우_해시_코드는_동일하다() {
        //given
        VoteHistoryId voteHistoryId = new VoteHistoryId(1L, 1L);
        VoteHistoryId voteHistoryId2 = new VoteHistoryId(1L, 1L);

        //when
        boolean equals = voteHistoryId.equals(voteHistoryId2);
        assertThat(equals).isTrue();
        boolean result = voteHistoryId.hashCode() == voteHistoryId2.hashCode();

        //then
        assertThat(result).isTrue();
    }
}