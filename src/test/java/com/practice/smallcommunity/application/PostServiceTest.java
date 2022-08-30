package com.practice.smallcommunity.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import com.practice.smallcommunity.application.dto.PostDto;
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
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    PostRepository postRepository;

    PostService postService;

    Category category = DomainGenerator.createCategory("dev", "개발");
    Member member = DomainGenerator.createMember("A");

    @BeforeEach
    void setUp() {
        postService = new PostService(postRepository);
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
    void 게시글을_수정한다() {
        //given
        Post post = DomainGenerator.createPost(category, member, "내용");

        when(postRepository.findById(1L))
            .thenReturn(Optional.of(post));

        //when
        Post updatedPost = postService.update(1L,
            new PostDto("new title", "new text"));

        //then
        assertThat(updatedPost.getTitle()).isEqualTo("new title");
        assertThat(updatedPost.getMainText().getText()).isEqualTo("new text");
    }

    @Test
    void 게시글을_삭제한다() {
        //given
        Post post = DomainGenerator.createPost(category, member, "내용");

        when(postRepository.findById(1L))
            .thenReturn(Optional.of(post));

        //when
        postService.disable(1L);

        //then
        assertThat(post.isEnable()).isFalse();
    }
}