package com.practice.smallcommunity.interfaces.member;

import static com.practice.smallcommunity.interfaces.RestDocsHelper.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.domain.post.Post;
import com.practice.smallcommunity.domain.post.PostRepository;
import com.practice.smallcommunity.domain.reply.Reply;
import com.practice.smallcommunity.domain.reply.ReplyRepository;
import com.practice.smallcommunity.interfaces.RestTest;
import com.practice.smallcommunity.interfaces.WithMockMember;
import com.practice.smallcommunity.utils.DomainGenerator;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@RestTest
@WebMvcTest(MemberContentController.class)
class MemberContentControllerTest {

    @MockBean
    PostRepository postRepository;

    @MockBean
    ReplyRepository replyRepository;

    @Autowired
    MockMvc mvc;

    Member member = DomainGenerator.createMember("A");
    Post post = DomainGenerator.createPost(null, member, "게시글");
    Reply reply = DomainGenerator.createReply(post, member, "답글");

    @BeforeEach
    void setUp() {
        post = spy(post);
        reply = spy(reply);
    }

    @Test
    @WithMockMember
    void 내가_쓴_게시글() throws Exception {
        //given
        when(post.getId()).thenReturn(1L);
        when(post.getCreatedDate()).thenReturn(LocalDateTime.now());
        when(postRepository.findPostsByWriter(eq(1L), any()))
            .thenReturn(new PageImpl<>(List.of(post)));

        //when
        ResultActions result = mvc.perform(get("/api/v1/members/posts")
            .accept(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk())
            .andDo(generateDocument("member",
                requestParameters(
                    parameterWithName("page").optional().description("페이지 번호"),
                    parameterWithName("size").optional().description("페이지 크기")
                ),
                responseFields(
                    pageData(),
                    fieldWithPath("postId").type(JsonFieldType.NUMBER).description("게시글 ID"),
                    fieldWithPath("title").type(JsonFieldType.STRING).description("게시글 제목"),
                    fieldWithPath("createdDate").type(JsonFieldType.STRING).description("게시글 작성일")
                )));
    }

    @Test
    @WithMockMember
    void 내가_답글을_쓴_게시글() throws Exception {
        //given
        when(post.getId()).thenReturn(1L);
        when(reply.getCreatedDate()).thenReturn(LocalDateTime.now());
        when(replyRepository.findByWriterFetchJoin(eq(1L), any()))
            .thenReturn(new PageImpl<>(List.of(reply)));

        //when
        ResultActions result = mvc.perform(get("/api/v1/members/replies")
            .accept(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk())
            .andDo(generateDocument("member",
                requestParameters(
                    parameterWithName("page").optional().description("페이지 번호"),
                    parameterWithName("size").optional().description("페이지 크기")
                ),
                responseFields(
                    pageData(),
                    fieldWithPath("postId").type(JsonFieldType.NUMBER).description("게시글 ID"),
                    fieldWithPath("title").type(JsonFieldType.STRING).description("게시글 제목"),
                    fieldWithPath("createdDate").type(JsonFieldType.STRING).description("답글 작성일")
                )));
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        MemberMapper memberMapper() {
            return new MemberMapperImpl();
        }
    }
}