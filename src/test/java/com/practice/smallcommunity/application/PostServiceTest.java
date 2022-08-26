package com.practice.smallcommunity.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import com.practice.smallcommunity.application.exception.ValidationError;
import com.practice.smallcommunity.application.exception.ValidationErrorException;
import com.practice.smallcommunity.domain.post.Post;
import com.practice.smallcommunity.domain.post.PostRepository;
import com.practice.smallcommunity.utils.DomainGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    BoardService boardService;

    @Mock
    MemberService memberService;

    @Mock
    PostRepository postRepository;

    PostService postService;

    @BeforeEach
    void setUp() {
        postService = new PostService(memberService, boardService, postRepository);
    }

    @Test
    void 게시글을_등록한다() {
        //given
        when(memberService.findByUserId(1L))
            .thenReturn(DomainGenerator.createMember("A"));
        when(postRepository.save(any(Post.class)))
            .thenAnswer(AdditionalAnswers.returnsFirstArg());

        //when
        Post wrotePost = postService.write(1L, 1L, "제목", "내용");

        //then
        assertThat(wrotePost).isNotNull();
    }

    @Test
    void 게시글을_등록하려는_게시판이_없으면_예외를_던진다() {
        //given
        when(boardService.findOne(1L))
            .thenThrow(new ValidationErrorException("", ValidationError.of("")));

        //when
        //then
        assertThatThrownBy(() -> postService.write(1L, 1L, "제목", "내용"))
            .isInstanceOf(ValidationErrorException.class);
    }

    @Test
    void 게시글을_작성하려는_회원이_존재하지_않으면_예외를_던진다() {
        //given
        when(memberService.findByUserId(1L))
            .thenThrow(new ValidationErrorException("", ValidationError.of("")));

        //when
        //then
        assertThatThrownBy(() -> postService.write(1L, 1L, "제목", "내용"))
            .isInstanceOf(ValidationErrorException.class);
    }
}