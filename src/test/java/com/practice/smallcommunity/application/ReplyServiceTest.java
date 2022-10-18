package com.practice.smallcommunity.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.practice.smallcommunity.application.exception.BusinessException;
import com.practice.smallcommunity.application.exception.ErrorCode;
import com.practice.smallcommunity.application.reply.ReplyService;
import com.practice.smallcommunity.domain.category.Category;
import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.domain.post.Post;
import com.practice.smallcommunity.domain.reply.Reply;
import com.practice.smallcommunity.domain.reply.ReplyRepository;
import com.practice.smallcommunity.utils.DomainGenerator;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReplyServiceTest {

    @Mock
    ReplyRepository replyRepository;

    ReplyService replyService;

    @Spy
    Member member = DomainGenerator.createMember("A");

    Category category = DomainGenerator.createCategory("dev", "개발");
    Post post = DomainGenerator.createPost(category, member, "내용");
    Reply dummyReply;

    @BeforeEach
    void setUp() {
        replyService = new ReplyService(replyRepository);
        dummyReply = DomainGenerator.createReply(post, member, "답글");
    }

    @Test
    void 답글을_추가한다() {
        //given
        when(replyRepository.save(any(Reply.class)))
            .thenReturn(dummyReply);

        //when
        Reply reply = replyService.add(dummyReply);

        //then
        assertThat(reply).isNotNull();
        assertThat(reply.isEnable()).isTrue();
    }

    @Test
    void 답글이_추가되면_게시글의_답글_개수가_증가한다() {
        //given
        when(replyRepository.save(any(Reply.class)))
            .thenReturn(dummyReply);

        //when
        Reply reply = replyService.add(dummyReply);

        //then
        assertThat(reply.getPost().getReplyCount()).isEqualTo(1);
    }

    @Test
    void 답글을_ID로_조회한다() {
        //given
        when(replyRepository.findByIdAndEnableIsTrue(1L))
            .thenReturn(Optional.of(dummyReply));

        //when
        Reply findReply = replyService.findEnabledReply(1L);

        //then
        assertThat(findReply).isNotNull();
    }

    @Test
    void 미삭제상태_답글을_ID로_조회할_때_해당하는_데이터가_없으면_예외를_던진다() {
        //given
        when(replyRepository.findByIdAndEnableIsTrue(1L))
            .thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> replyService.findEnabledReply(1L))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_FOUND_REPLY);
    }

    @Test
    void 게시글의_답글목록을_조회한다() {
        //given
        Reply dummyReply2 = DomainGenerator.createReply(post, member, "내용1");

        when(replyRepository.findByPostAndEnableIsTrue(post))
            .thenReturn(List.of(dummyReply, dummyReply2));

        //when
        List<Reply> replies = replyService.findRepliesOnPost(post);

        //then
        assertThat(replies.size()).isEqualTo(2);
    }

    @Test
    void 답글을_수정한다() {
        //given
        when(member.getId()).thenReturn(1L);
        when(replyRepository.findByIdAndEnableIsTrue(1L))
            .thenReturn(Optional.of(dummyReply));

        //when
        Reply updatedReply = replyService.update(1L, 1L, "새로운 내용");

        //then
        assertThat(updatedReply.getText()).isEqualTo("새로운 내용");
    }

    @Test
    void 답글을_수정하는_회원이_답글_작성자가_아니면_예외를_던진다() {
        //given
        when(member.getId()).thenReturn(2L);
        when(replyRepository.findByIdAndEnableIsTrue(1L))
            .thenReturn(Optional.of(dummyReply));

        //when
        //then
        assertThatThrownBy(() -> replyService.update(1L, 1L, "새로운 내용"))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ACCESS_DENIED);
    }

    @Test
    void 답글을_삭제상태로_만든다() {
        //given
        when(member.getId()).thenReturn(1L);
        when(replyRepository.findByIdAndEnableIsTrue(1L))
            .thenReturn(Optional.of(dummyReply));

        //when
        replyService.disable(1L, 1L);

        //then
        assertThat(dummyReply.isEnable()).isFalse();
    }

    @Test
    void 추가됐던_답글이_삭제상태로_변경되면_게시글의_답글_개수가_감소한다() {
        //given
        when(member.getId()).thenReturn(1L);
        when(replyRepository.save(any(Reply.class)))
            .thenReturn(dummyReply);
        when(replyRepository.findByIdAndEnableIsTrue(1L))
            .thenReturn(Optional.of(dummyReply));

        //when
        Reply reply = replyService.add(dummyReply);
        assertThat(reply.getPost().getReplyCount()).isEqualTo(1);

        replyService.disable(1L, 1L);

        //then
        assertThat(reply.getPost().getReplyCount()).isEqualTo(0);
    }

    @Test
    void 답글을_삭제하는_회원이_답글_작성자가_아니면_예외를_던진다() {
        //given
        when(member.getId()).thenReturn(2L);
        when(replyRepository.findByIdAndEnableIsTrue(1L))
            .thenReturn(Optional.of(dummyReply));

        //when
        assertThatThrownBy(() -> replyService.disable(1L, 1L))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ACCESS_DENIED);
    }
}