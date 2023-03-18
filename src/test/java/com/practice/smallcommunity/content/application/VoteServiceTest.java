package com.practice.smallcommunity.content.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import com.practice.smallcommunity.content.VoteHistoryService;
import com.practice.smallcommunity.content.VoteService;
import com.practice.smallcommunity.notification.NotificationService;
import com.practice.smallcommunity.post.application.PostService;
import com.practice.smallcommunity.reply.ReplyService;
import com.practice.smallcommunity.content.domain.Content;
import com.practice.smallcommunity.member.Member;
import com.practice.smallcommunity.post.domain.Post;
import com.practice.smallcommunity.reply.Reply;
import com.practice.smallcommunity.testutils.DomainGenerator;
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

    @Mock
    NotificationService notificationService;

    VoteService voteService;

    Member dummyMember;

    Content dummyContent;

    @BeforeEach
    void setUp() {
        dummyMember = spy(DomainGenerator.createMember("A"));
        dummyContent = spy(DomainGenerator.createContent(dummyMember));

        voteService = new VoteService(postService, replyService, voteHistoryService, notificationService);
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