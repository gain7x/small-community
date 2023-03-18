package com.practice.smallcommunity.content.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.practice.smallcommunity.content.VoteHistoryService;
import com.practice.smallcommunity.content.domain.Content;
import com.practice.smallcommunity.content.domain.VoteHistory;
import com.practice.smallcommunity.content.domain.VoteHistoryRepository;
import com.practice.smallcommunity.member.domain.Member;
import com.practice.smallcommunity.testutils.DomainGenerator;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VoteHistoryServiceTest {

    @Mock
    VoteHistoryRepository voteHistoryRepository;

    VoteHistoryService voteHistoryService;

    Member dummyMember;

    Content dummyContent;

    @BeforeEach
    void setUp() {
        dummyMember = spy(DomainGenerator.createMember("A"));
        dummyContent = spy(DomainGenerator.createContent(dummyMember));

        when(dummyMember.getId()).thenReturn(1L);
        when(dummyContent.getId()).thenReturn(1L);

        voteHistoryService = new VoteHistoryService(voteHistoryRepository);
    }

    @Test
    void 이전_투표기록이_없으면_투표기록을_추가한다() {
        //given
        when(voteHistoryRepository.findById(any()))
            .thenReturn(Optional.empty());

        //when
        boolean result = voteHistoryService.addVoteHistory(dummyMember, dummyContent, true);

        //then
        verify(voteHistoryRepository, times(1)).save(any());
        assertThat(result).isTrue();
    }

    @Test
    void 추가하려는_투표가_이전_투표기록과_동일하면_추가되지_않는다() {
        //given
        VoteHistory prevVoteHistory = VoteHistory.builder()
            .voter(dummyMember)
            .content(dummyContent)
            .positive(true)
            .build();

        when(voteHistoryRepository.findById(any()))
            .thenReturn(Optional.of(prevVoteHistory));

        //when
        boolean result = voteHistoryService.addVoteHistory(dummyMember, dummyContent, true);

        //then
        assertThat(result).isFalse();
    }

    @Test
    void 추가하려는_투표가_이전_투표기록과_다르면_이전_투표기록을_삭제한다() {
        //given
        VoteHistory prevVoteHistory = VoteHistory.builder()
            .voter(dummyMember)
            .content(dummyContent)
            .positive(false)
            .build();

        when(voteHistoryRepository.findById(any()))
            .thenReturn(Optional.of(prevVoteHistory));

        //when
        boolean result = voteHistoryService.addVoteHistory(dummyMember, dummyContent, true);

        //then
        verify(voteHistoryRepository, times(1)).delete(any());
        assertThat(result).isTrue();
    }
}