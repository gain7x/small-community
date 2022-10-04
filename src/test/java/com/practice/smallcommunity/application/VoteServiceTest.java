package com.practice.smallcommunity.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.practice.smallcommunity.domain.content.Content;
import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.domain.post.Post;
import com.practice.smallcommunity.domain.reply.Reply;
import com.practice.smallcommunity.utils.DomainGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VoteServiceTest {

    @Mock
    PostService postService;

    @Mock
    ReplyService replyService;

    @Mock
    VoteHistoryService voteHistoryService;

    VoteService voteService;

    Member dummyMember;

    Content dummyContent;

    @BeforeEach
    void setUp() {
        dummyMember = spy(DomainGenerator.createMember("A"));
        dummyContent = spy(DomainGenerator.createContent(dummyMember));

        voteService = new VoteService(postService, replyService, voteHistoryService);
    }

    @Test
    void 답글을_투표한다() {
        //given
        Reply findReply = Reply.builder()
            .writer(dummyMember)
            .build();

        when(replyService.findEnabledReply(1L))
            .thenReturn(findReply);

        when(voteHistoryService.addVoteHistory(eq(null), any(), eq(true)))
            .thenReturn(true);

        //when
        boolean result = voteService.voteReply(1L, null, true);

        //then
        assertThat(result).isTrue();
        assertThat(findReply.getVotes()).isEqualTo(1);
    }

    @Test
    void 게시글을_투표한다() {
        //given
        Post findPost = Post.builder()
            .writer(dummyMember)
            .build();

        when(postService.findPost(1L))
            .thenReturn(findPost);

        when(voteHistoryService.addVoteHistory(eq(null), any(), eq(true)))
            .thenReturn(true);

        //when
        boolean result = voteService.votePost(1L, null, true);

        //then
        assertThat(result).isTrue();
        assertThat(findPost.getVotes()).isEqualTo(1);
    }
}