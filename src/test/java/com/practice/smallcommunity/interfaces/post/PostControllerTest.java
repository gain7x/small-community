package com.practice.smallcommunity.interfaces.post;

import static com.practice.smallcommunity.interfaces.RestDocsHelper.baseData;
import static com.practice.smallcommunity.interfaces.RestDocsHelper.generateDocument;
import static com.practice.smallcommunity.interfaces.RestDocsHelper.getConstrainedFields;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.smallcommunity.application.CategoryService;
import com.practice.smallcommunity.application.MemberService;
import com.practice.smallcommunity.application.PostService;
import com.practice.smallcommunity.application.VoteService;
import com.practice.smallcommunity.application.dto.PostDto;
import com.practice.smallcommunity.domain.category.Category;
import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.domain.post.Post;
import com.practice.smallcommunity.interfaces.RestDocsHelper.ConstrainedFields;
import com.practice.smallcommunity.interfaces.RestTest;
import com.practice.smallcommunity.interfaces.WithMockMember;
import com.practice.smallcommunity.interfaces.content.dto.VoteRequest;
import com.practice.smallcommunity.interfaces.post.dto.PostRequest;
import com.practice.smallcommunity.interfaces.post.dto.PostUpdateRequest;
import com.practice.smallcommunity.utils.DomainGenerator;
import java.time.LocalDateTime;
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
@WebMvcTest(PostController.class)
class PostControllerTest {

    @MockBean
    CategoryService categoryService;

    @MockBean
    MemberService memberService;

    @MockBean
    PostService postService;

    @MockBean
    VoteService voteService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mvc;

    Category dummyCategory = DomainGenerator.createCategory("dev", "개발");
    Member dummyMember = DomainGenerator.createMember("A");

    @Test
    @WithMockMember
    void 게시글등록() throws Exception {
        //given
        when(categoryService.findOne("dev"))
            .thenReturn(dummyCategory);

        when(memberService.findByUserId(1L))
            .thenReturn(dummyMember);

        Post post = DomainGenerator.createPost(dummyCategory, dummyMember, "내용");
        post = spy(post);

        when(post.getId()).thenReturn(1L);

        when(postService.write(eq(dummyCategory), eq(dummyMember), any(PostDto.class)))
            .thenReturn(post);

        //when
        PostRequest dto = PostRequest.builder()
            .categoryCode("dev")
            .title("제목")
            .text("내용")
            .build();

        ResultActions result = mvc.perform(post("/api/v1/posts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto))
            .accept(MediaType.APPLICATION_JSON));

        ConstrainedFields fields = getConstrainedFields(PostRequest.class);

        //then
        result.andExpect(status().isCreated())
            .andDo(generateDocument("post", requestFields(
                    fields.withPath("categoryCode").type(JsonFieldType.STRING).description("카테고리 코드"),
                    fields.withPath("title").type(JsonFieldType.STRING).description("게시글 제목"),
                    fields.withPath("text").type(JsonFieldType.STRING).description("게시글 내용")
                ),
                responseFields(
                    baseData(),
                    fieldWithPath("postId").description(JsonFieldType.NUMBER)
                        .description("등록된 게시글의 번호")
                )));
    }

    @Test
    @WithMockMember
    void 게시글조회() throws Exception {
        //given
        Category spyCategory = spy(dummyCategory);
        when(spyCategory.getId()).thenReturn(1L);

        Member spyMember = spy(dummyMember);
        when(spyMember.getId()).thenReturn(1L);

        Post post = DomainGenerator.createPost(spyCategory, spyMember, "내용");
        post = spy(post);

        when(post.getCreatedDate()).thenReturn(LocalDateTime.now());

        when(postService.viewPost(1L))
            .thenReturn(post);

        //when
        ResultActions result = mvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/posts/{postId}", 1L)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk())
            .andDo(generateDocument("post",
                pathParameters(
                    parameterWithName("postId").description("게시글 번호")
                ),
                responseFields(
                    baseData(),
                    fieldWithPath("categoryCode").type(JsonFieldType.STRING).description("카테고리 코드"),
                    fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("작성자 ID"),
                    fieldWithPath("nickname").type(JsonFieldType.STRING).description("작성자 닉네임"),
                    fieldWithPath("title").type(JsonFieldType.STRING).description("게시글 제목"),
                    fieldWithPath("text").type(JsonFieldType.STRING).description("게시글 내용"),
                    fieldWithPath("views").type(JsonFieldType.NUMBER).description("게시글 조회수"),
                    fieldWithPath("votes").type(JsonFieldType.NUMBER).description("게시글 투표수"),
                    fieldWithPath("acceptId").type(JsonFieldType.NUMBER).optional().description("채택한 답글 ID"),
                    fieldWithPath("createdDate").type(JsonFieldType.STRING).description("작성일")
                )));
    }

    @Test
    @WithMockMember
    void 게시글수정() throws Exception {
        //given
        Post post = DomainGenerator.createPost(dummyCategory, dummyMember, "내용");

        when(postService.update(eq(1L), eq(1L), any(PostDto.class)))
            .thenReturn(post);

        //when
        PostUpdateRequest dto = PostUpdateRequest.builder()
            .title("신규 제목")
            .text("신규 내용")
            .build();

        ResultActions result = mvc.perform(
            RestDocumentationRequestBuilders.patch("/api/v1/posts/{postId}", 1L)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto))
            .accept(MediaType.APPLICATION_JSON));

        ConstrainedFields fields = getConstrainedFields(PostRequest.class);

        //then
        result.andExpect(status().isNoContent())
            .andDo(generateDocument("post",
                pathParameters(
                    parameterWithName("postId").description("게시글 번호")
                ),
                requestFields(
                    fields.withPath("title").type(JsonFieldType.STRING).description("게시글 제목"),
                    fields.withPath("text").type(JsonFieldType.STRING).description("게시글 내용")
                )));
    }

    @Test
    @WithMockMember
    void 게시글삭제() throws Exception {
        //when
        ResultActions result = mvc.perform(
            RestDocumentationRequestBuilders.delete("/api/v1/posts/{postId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        //then
        verify(postService, only()).disable(1L, 1L);

        result.andExpect(status().isNoContent())
            .andDo(generateDocument("post", pathParameters(
                parameterWithName("postId").description("게시글 번호")
            )));
    }

    @Test
    @WithMockMember
    void 게시글투표() throws Exception {
        //when
        VoteRequest dto = VoteRequest.builder()
            .positive(true)
            .build();

        ResultActions result = mvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/posts/{postId}/vote", 1L)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto))
            .accept(MediaType.APPLICATION_JSON));

        //then
        ConstrainedFields fields = getConstrainedFields(VoteRequest.class);

        result.andExpect(status().isOk())
            .andDo(generateDocument("post",
                pathParameters(
                    parameterWithName("postId").description("게시글 번호")
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
        PostMapper postMapper() {
            return new PostMapperImpl();
        }
    }
}