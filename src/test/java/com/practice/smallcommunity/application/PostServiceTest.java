package com.practice.smallcommunity.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import com.practice.smallcommunity.application.dto.PostDto;
import com.practice.smallcommunity.application.exception.BusinessException;
import com.practice.smallcommunity.application.exception.ErrorCode;
import com.practice.smallcommunity.domain.category.Category;
import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.domain.post.Post;
import com.practice.smallcommunity.domain.post.PostRepository;
import com.practice.smallcommunity.utils.DomainGenerator;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    PostRepository postRepository;

    PostService postService;

    Category category = DomainGenerator.createCategory("dev", "개발");

    @Spy
    Member member = DomainGenerator.createMember("A");

    Post dummyPost;

    @BeforeEach
    void setUp() {
        postService = new PostService(postRepository);
        dummyPost = DomainGenerator.createPost(category, member, "내용");
    }

    @Test
    void 게시글을_등록한다() {
        //given
        when(postRepository.save(any(Post.class)))
            .thenAnswer(AdditionalAnswers.returnsFirstArg());

        //when
        Post wrotePost = postService.write(category, member,
            new PostDto("제목", "내용"));

        //then
        assertThat(wrotePost).isNotNull();
    }

    @Test
    void 미삭제상태_게시글을_ID로_조회한다() {
        //given
        when(postRepository.findByIdAndEnableIsTrue(1L))
            .thenReturn(Optional.of(dummyPost));

        //when
        Post findPost = postService.findPost(1L);

        //then
        assertThat(findPost).isNotNull();
        assertThat(findPost.getTitle()).isEqualTo(dummyPost.getTitle());
    }

    @Test
    void 미삭제상태_게시글을_ID로_조회할_때_해당하는_데이터가_없으면_예외를_던진다() {
        //given
        when(postRepository.findByIdAndEnableIsTrue(1L))
            .thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> postService.findPost(1L))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_FOUND_POST);
    }

    @Test
    void 미삭제상태_게시글을_ID로_조회하고_조회수를_증가시킨다() {
        //given
        when(postRepository.findPostWithMainText(1L))
            .thenReturn(Optional.of(dummyPost));

        //when
        Post findPost = postService.viewPost(1L);

        //then
        assertThat(findPost.getViews()).isEqualTo(1);
    }

    @Test
    void 게시글을_수정한다() {
        //given
        when(member.getId()).thenReturn(1L);
        when(postRepository.findPostWithMainText(1L))
            .thenReturn(Optional.of(dummyPost));

        //when
        Post updatedPost = postService.update(1L, 1L,
            new PostDto("new title", "new text"));

        //then
        assertThat(updatedPost.getTitle()).isEqualTo("new title");
        assertThat(updatedPost.getMainText().getText()).isEqualTo("new text");
    }

    @Test
    void 게시글을_수정하는_회원이_게시글_작성자가_아니면_예외를_던진다() {
        //given
        when(member.getId()).thenReturn(2L);
        when(postRepository.findPostWithMainText(1L))
            .thenReturn(Optional.of(dummyPost));

        //when
        assertThatThrownBy(() -> postService.update(1L, 1L,
            new PostDto("new title", "new text")))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ACCESS_DENIED);
    }

    @Test
    void 게시글을_삭제한다() {
        //given
        when(member.getId()).thenReturn(1L);
        when(postRepository.findByIdAndEnableIsTrue(1L))
            .thenReturn(Optional.of(dummyPost));

        //when
        postService.disable(1L, 1L);

        //then
        assertThat(dummyPost.isEnable()).isFalse();
    }

    @Test
    void 게시글을_삭제하는_회원이_게시글_작성자가_아니면_예외를_던진다() {
        //given
        when(member.getId()).thenReturn(2L);
        when(postRepository.findByIdAndEnableIsTrue(1L))
            .thenReturn(Optional.of(dummyPost));

        //when
        assertThatThrownBy(() -> postService.disable(1L, 1L))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ACCESS_DENIED);
    }
}