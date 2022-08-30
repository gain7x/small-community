package com.practice.smallcommunity.interfaces.post;

import static com.practice.smallcommunity.interfaces.RestDocsHelper.generateDocument;
import static com.practice.smallcommunity.interfaces.RestDocsHelper.getConstrainedFields;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.smallcommunity.application.CategoryService;
import com.practice.smallcommunity.application.MemberService;
import com.practice.smallcommunity.application.PostService;
import com.practice.smallcommunity.application.dto.PostDto;
import com.practice.smallcommunity.domain.category.Category;
import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.domain.post.Post;
import com.practice.smallcommunity.interfaces.RestDocsHelper.ConstrainedFields;
import com.practice.smallcommunity.interfaces.post.dto.PostRequest;
import com.practice.smallcommunity.interfaces.post.dto.PostUpdateRequest;
import com.practice.smallcommunity.utils.DomainGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@AutoConfigureRestDocs
@WebMvcTest(PostController.class)
class PostControllerTest {

    @MockBean
    CategoryService categoryService;

    @MockBean
    MemberService memberService;

    @MockBean
    PostService postService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mvc;

    Category category = DomainGenerator.createCategory("dev", "개발");
    Member member = DomainGenerator.createMember("A");
    Post dummyPost = DomainGenerator.createPost(category, member, "내용");

    @Test
    @WithMockUser
    void 게시글등록() throws Exception {
        //given
        when(categoryService.findOne(1L))
            .thenReturn(category);

        when(memberService.findByUserId(1L))
            .thenReturn(member);

        when(postService.write(eq(category), eq(member), any(PostDto.class)))
            .thenReturn(dummyPost);

        //when
        PostRequest dto = PostRequest.builder()
            .memberId(1L)
            .categoryId(1L)
            .title("제목")
            .text("내용")
            .build();

        ResultActions result = mvc.perform(post("/api/v1/posts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto))
            .accept(MediaType.APPLICATION_JSON)
            .with(csrf()));

        ConstrainedFields fields = getConstrainedFields(PostRequest.class);

        //then
        result.andExpect(status().isCreated())
            .andDo(generateDocument("post", requestFields(
                fields.withPath("categoryId").type(JsonFieldType.NUMBER).description("카테고리 번호"),
                fields.withPath("memberId").type(JsonFieldType.NUMBER).description("회원 번호"),
                fields.withPath("title").type(JsonFieldType.STRING).description("게시글 제목"),
                fields.withPath("text").type(JsonFieldType.STRING).description("게시글 내용")
            )));
    }

    @Test
    @WithMockUser
    void 게시글조회() throws Exception {
        //given
        when(postService.findEnabledPost(1L))
            .thenReturn(dummyPost);

        //when
        ResultActions result = mvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/posts/{postId}", 1L)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .with(csrf()));

        //then
        result.andExpect(status().isOk())
            .andDo(generateDocument("post", pathParameters(
                parameterWithName("postId").description("게시글 번호")
            )));
    }

    @Test
    @WithMockUser
    void 게시글수정() throws Exception {
        //given
        when(postService.update(eq(1L), any(PostDto.class)))
            .thenReturn(dummyPost);

        //when
        PostUpdateRequest dto = PostUpdateRequest.builder()
            .title("신규 제목")
            .text("신규 내용")
            .build();

        ResultActions result = mvc.perform(
            RestDocumentationRequestBuilders.patch("/api/v1/posts/{postId}", 1L)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto))
            .accept(MediaType.APPLICATION_JSON)
            .with(csrf()));

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
    @WithMockUser
    void 게시글삭제() throws Exception {
        //given
        when(postService.findEnabledPost(1L))
            .thenReturn(dummyPost);

        //when
        ResultActions result = mvc.perform(
            RestDocumentationRequestBuilders.delete("/api/v1/posts/{postId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf()));

        //then
        result.andExpect(status().isNoContent())
            .andDo(generateDocument("post", pathParameters(
                parameterWithName("postId").description("게시글 번호")
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