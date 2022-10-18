package com.practice.smallcommunity.interfaces.post;

import static com.practice.smallcommunity.interfaces.RestDocsHelper.generateDocument;
import static com.practice.smallcommunity.interfaces.RestDocsHelper.pageData;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.practice.smallcommunity.application.category.CategoryService;
import com.practice.smallcommunity.domain.category.Category;
import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.domain.post.Post;
import com.practice.smallcommunity.domain.post.PostSearchRepository;
import com.practice.smallcommunity.domain.post.dto.BoardSearchCond;
import com.practice.smallcommunity.interfaces.RestTest;
import com.practice.smallcommunity.utils.DomainGenerator;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@RestTest
@WebMvcTest(PostSearchController.class)
class PostSearchControllerTest {

    @MockBean
    CategoryService categoryService;

    @MockBean
    PostSearchRepository postSearchRepository;

    @Autowired
    MockMvc mvc;

    Member member = DomainGenerator.createMember("A");
    Category category = DomainGenerator.createCategory("dev", "개발");

    @Test
    void 게시글_목록_검색() throws Exception {
        //given
        Category spyCategory = spy(category);
        when(spyCategory.getId()).thenReturn(1L);

        when(categoryService.findOne("dev")).thenReturn(spyCategory);

        Member spyMember = spy(this.member);
        when(spyMember.getId()).thenReturn(1L);

        Post spyPost = spy(DomainGenerator.createPost(category, spyMember, "내용"));
        when(spyPost.getId()).thenReturn(1L);
        when(spyPost.getCreatedDate()).thenReturn(LocalDateTime.now());

        Page<Post> posts = new PageImpl<>(List.of(spyPost));

        when(postSearchRepository.searchPosts(any(BoardSearchCond.class), any(Pageable.class)))
            .thenReturn(posts);

        //when
        ResultActions result = mvc.perform(get("/api/v1/posts")
            .param("categoryCode", "dev")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk())
            .andDo(generateDocument("board",
                requestParameters(
                    parameterWithName("categoryCode").description("카테고리 코드"),
                    parameterWithName("title").optional().description("검색할 제목, 최소 2글자"),
                    parameterWithName("page").optional().description("페이지 번호"),
                    parameterWithName("size").optional().description("페이지 크기")
                ),
                responseFields(
                    pageData(),
                    fieldWithPath("postId").type(JsonFieldType.NUMBER).description("게시글 ID"),
                    fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("회원 ID"),
                    fieldWithPath("nickname").type(JsonFieldType.STRING).description("작성자 닉네임"),
                    fieldWithPath("title").type(JsonFieldType.STRING).description("게시글 제목"),
                    fieldWithPath("views").type(JsonFieldType.NUMBER).description("게시글 조회수"),
                    fieldWithPath("replyCount").type(JsonFieldType.NUMBER).description("게시글의 답글 개수"),
                    fieldWithPath("votes").type(JsonFieldType.NUMBER).description("게시글 투표수"),
                    fieldWithPath("acceptId").type(JsonFieldType.NUMBER).optional().description("채택한 답글 ID"),
                    fieldWithPath("createdDate").type(JsonFieldType.STRING).description("작성일")
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