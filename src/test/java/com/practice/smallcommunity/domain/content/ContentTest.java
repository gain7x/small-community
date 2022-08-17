package com.practice.smallcommunity.domain.content;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ContentTest {

    Content content = Content.builder()
        .text("컨텐츠")
        .build();

    @Test
    void 투표추가() {
        //when
        content.vote(true);

        //then
        assertThat(content.getTotalVote()).isEqualTo(1);
    }

    @Test
    void 투표감소() {
        //when
        content.vote(false);

        //then
        assertThat(content.getTotalVote()).isEqualTo(-1);
    }
}