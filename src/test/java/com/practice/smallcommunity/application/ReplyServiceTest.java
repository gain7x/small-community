package com.practice.smallcommunity.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.practice.smallcommunity.application.exception.ValidationErrorException;
import com.practice.smallcommunity.domain.category.Category;
import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.domain.post.Post;
import com.practice.smallcommunity.domain.reply.Reply;
import com.practice.smallcommunity.domain.reply.ReplyRepository;
import com.practice.smallcommunity.utils.DomainGenerator;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReplyServiceTest {

    @Mock
    ReplyRepository replyRepository;

    ReplyService replyService;

    Member member = DomainGenerator.createMember("A");
    Category category = DomainGenerator.createCategory("dev", "개발");
    Post post = DomainGenerator.createPost(category, member, "내용");
    Reply dummyReply = DomainGenerator.createReply(post, member, "답글");

    @BeforeEach
    void setUp() {
        replyService = new ReplyService(replyRepository);
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
    void 답글을_ID로_조회한다() {
        //given
        when(replyRepository.findById(1L))
            .thenReturn(Optional.of(dummyReply));

        //when
        Reply findReply = replyService.findEnabledReply(1L);

        //then
        assertThat(findReply).isNotNull();
    }

    @Test
    void 답글을_ID로_조회할_때_일치하는_ID가_없으면_예외를_던진다() {
        //given
        when(replyRepository.findById(1L))
            .thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> replyService.findEnabledReply(1L))
            .isInstanceOf(ValidationErrorException.class);
    }

    @Test
    void 답글을_ID로_조회했는데_삭제상태이면_예외를_던진다() {
        //given
        dummyReply.delete();

        when(replyRepository.findById(1L))
            .thenReturn(Optional.of(dummyReply));

        //when
        //then
        assertThatThrownBy(() -> replyService.findEnabledReply(1L))
            .isInstanceOf(ValidationErrorException.class);
    }

    @Test
    void 답글을_수정한다() {
        //given
        when(replyRepository.findById(1L))
            .thenReturn(Optional.of(dummyReply));

        //when
        Reply updatedReply = replyService.update(1L, "새로운 내용");

        //then
        assertThat(updatedReply.getText()).isEqualTo("새로운 내용");
    }

    @Test
    void 답글을_삭제상태로_만든다() {
        //given
        when(replyRepository.findById(1L))
            .thenReturn(Optional.of(dummyReply));

        //when
        replyService.disable(1L);

        //then
        assertThat(dummyReply.isEnable()).isFalse();
    }
}