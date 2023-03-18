package com.practice.smallcommunity.reply.interfaces;

import static com.practice.smallcommunity.testutils.interfaces.RestDocsHelper.ConstrainedFields;
import static com.practice.smallcommunity.testutils.interfaces.RestDocsHelper.baseData;
import static com.practice.smallcommunity.testutils.interfaces.RestDocsHelper.collectionData;
import static com.practice.smallcommunity.testutils.interfaces.RestDocsHelper.generateDocument;
import static com.practice.smallcommunity.testutils.interfaces.RestDocsHelper.getConstrainedFields;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.smallcommunity.member.application.MemberService;
import com.practice.smallcommunity.post.application.PostService;
import com.practice.smallcommunity.reply.ReplyService;
import com.practice.smallcommunity.content.VoteService;
import com.practice.smallcommunity.category.Category;
import com.practice.smallcommunity.member.domain.Member;
import com.practice.smallcommunity.post.domain.Post;
import com.practice.smallcommunity.reply.Reply;
import com.practice.smallcommunity.testutils.interfaces.RestTest;
import com.practice.smallcommunity.testutils.interfaces.WithMockMember;
import com.practice.smallcommunity.content.interfaces.dto.VoteRequest;
import com.practice.smallcommunity.reply.interfaces.dto.ReplyAddRequest;
import com.practice.smallcommunity.reply.interfaces.dto.ReplyUpdateRequest;
import com.practice.smallcommunity.testutils.DomainGenerator;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@RestTest
@WebMvcTest(ReplyController.class)
class ReplyControllerTest {

    @MockBean
    MemberService memberService;

    @MockBean
    PostService postService;

    @MockBean
    ReplyService replyService;

    @MockBean
    VoteService voteService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mvc;

    Member postWriter = DomainGenerator.createMember("A");
    Category category = DomainGenerator.createCategory("dev", "개발");
    Post post = DomainGenerator.createPost(category, postWriter, "내용");

    Member replyWriter;
    Reply dummyReply;

    @BeforeEach
    void setUp() {
        when(postService.findPost(1L))
            .thenReturn(post);

        when(memberService.findByUserId(1L))
            .thenReturn(postWriter);

        replyWriter = DomainGenerator.createMember("B");
        replyWriter = spy(replyWriter);
        dummyReply = DomainGenerator.createReply(post, replyWriter, "답글");
        dummyReply = spy(dummyReply);

        when(replyWriter.getId())
            .thenReturn(1L);

        when(dummyReply.getId())
            .thenReturn(1L);

        when(dummyReply.getCreatedDate())
            .thenReturn(LocalDateTime.now());
    }

    @Test
    @WithMockMember
    void 게시글에_답글추가() throws Exception {
        //given
        when(replyService.add(any(Reply.class)))
            .thenReturn(dummyReply);

        //when
        ReplyAddRequest dto = ReplyAddRequest.builder()
            .postId(1L)
            .text("답글")
            .build();

        ResultActions result = mvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/replies", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .accept(MediaType.APPLICATION_JSON));

        //then
        ConstrainedFields fields = getConstrainedFields(ReplyAddRequest.class);

        result.andExpect(status().isCreated())
            .andDo(generateDocument("reply",
                requestFields(
                    fields.withPath("postId").type(JsonFieldType.NUMBER).description("대상 게시글 ID"),
                    fields.withPath("text").type(JsonFieldType.STRING).description("답글 내용")
                ),
                responseFields(
                    baseData(),
                    fieldWithPath("replyId").type(JsonFieldType.NUMBER).description("답글 ID"),
                    fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("회원 ID"),
                    fieldWithPath("nickname").type(JsonFieldType.STRING).description("회원 닉네임"),
                    fieldWithPath("text").type(JsonFieldType.STRING).description("내용"),
                    fieldWithPath("votes").type(JsonFieldType.NUMBER).description("투표수"),
                    fieldWithPath("createdDate").type(JsonFieldType.STRING).description("작성일")
                )));
    }

    @Test
    @WithMockMember
    void 게시글의_답글목록_조회() throws Exception{
        //given
        when(replyService.findRepliesOnPost(any(Post.class)))
            .thenReturn(List.of(dummyReply));

        //when
        ResultActions result = mvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/replies")
                .param("postId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk())
            .andDo(generateDocument("reply",
                requestParameters(
                    parameterWithName("postId").description("게시글 ID")
                ),
                responseFields(
                    collectionData(),
                    fieldWithPath("replyId").type(JsonFieldType.NUMBER).description("답글 ID"),
                    fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("회원 ID"),
                    fieldWithPath("nickname").type(JsonFieldType.STRING).description("회원 닉네임"),
                    fieldWithPath("text").type(JsonFieldType.STRING).description("내용"),
                    fieldWithPath("votes").type(JsonFieldType.NUMBER).description("투표수"),
                    fieldWithPath("createdDate").type(JsonFieldType.STRING).description("작성일")
                )));
    }

    @Test
    @WithMockMember
    void 답글수정() throws Exception {
        //given
        when(replyService.update(eq(1L), eq(1L), any(String.class)))
            .thenReturn(dummyReply);

        //when
        ReplyUpdateRequest dto = ReplyUpdateRequest.builder()
            .text("새로운 내용")
            .build();

        ResultActions result = mvc.perform(
            RestDocumentationRequestBuilders.patch("/api/v1/replies/{replyId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .accept(MediaType.APPLICATION_JSON));

        //then
        ConstrainedFields fields = getConstrainedFields(ReplyUpdateRequest.class);

        result.andExpect(status().isNoContent())
            .andDo(generateDocument("reply",
                pathParameters(
                    parameterWithName("replyId").description("답글 ID")
                ),
                requestFields(
                    fields.withPath("text").type(JsonFieldType.STRING).description("새로운 답글 내용")
                )));
    }

    @Test
    @WithMockMember
    void 답글삭제() throws Exception {
        //when
        ResultActions result = mvc.perform(
            RestDocumentationRequestBuilders.delete("/api/v1/replies/{replyId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        //then
        verify(replyService, only()).disable(1L, 1L);

        result.andExpect(status().isNoContent())
            .andDo(generateDocument("reply",
                pathParameters(
                    parameterWithName("replyId").description("답글 ID")
                )));
    }

    @Test
    @WithMockMember
    void 답글투표() throws Exception {
        //when
        VoteRequest dto = VoteRequest.builder()
            .positive(true)
            .build();

        ResultActions result = mvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/replies/{replyId}/vote", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .accept(MediaType.APPLICATION_JSON));

        //then
        ConstrainedFields fields = getConstrainedFields(VoteRequest.class);

        result.andExpect(status().isOk())
            .andDo(generateDocument("reply",
                pathParameters(
                    parameterWithName("replyId").description("답글 번호")
                ),
                requestFields(
                    fields.withPath("positive").type(JsonFieldType.BOOLEAN).description("긍정 여부")
                ),
                responseFields(
                    baseData(),
                    fieldWithPath("voted").type(JsonFieldType.BOOLEAN).description("투표수 변화 여부")
                )));
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        ReplyMapper replyMapper() {
            return new ReplyMapperImpl();
        }
    }
}