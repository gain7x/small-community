package com.practice.smallcommunity.interfaces.post;

import static com.practice.smallcommunity.interfaces.RestDocsHelper.generateDocument;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.practice.smallcommunity.application.BoardService;
import com.practice.smallcommunity.domain.category.Category;
import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.domain.post.Post;
import com.practice.smallcommunity.domain.post.dto.BoardSearchCond;
import com.practice.smallcommunity.interfaces.RestTest;
import com.practice.smallcommunity.utils.DomainGenerator;
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
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@RestTest
@WebMvcTest(BoardController.class)
class BoardControllerTest {

    @MockBean
    BoardService boardService;

    @Autowired
    MockMvc mvc;

    Member member = DomainGenerator.createMember("A");
    Category category = DomainGenerator.createCategory("dev", "개발");

    @Test
    void 게시글_목록_검색() throws Exception {
        //given
        Member spyMember = spy(this.member);
        when(spyMember.getId()).thenReturn(1L);

        Post post = DomainGenerator.createPost(category, spyMember, "내용");
        Post spyPost = spy(post);
        when(spyPost.getId()).thenReturn(1L);

        Page<Post> posts = new PageImpl<>(List.of(spyPost));

        when(boardService.searchPostsInCategory(any(BoardSearchCond.class), any(Pageable.class)))
            .thenReturn(posts);

        //when
        ResultActions result = mvc.perform(RestDocumentationRequestBuilders.get(
                "/api/v1/categories/{categoryId}/posts", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk())
            .andDo(generateDocument("board",
                pathParameters(
                    parameterWithName("categoryId").description("카테고리 번호")
                ),
                requestParameters(
                    parameterWithName("title").optional().description("검색할 제목"),
                    parameterWithName("text").optional().description("검색할 내용"),
                    parameterWithName("page").optional().description("페이지 번호"),
                    parameterWithName("size").optional().description("페이지 크기")
                ),
                responseFields(
                    fieldWithPath("reason").type(JsonFieldType.STRING).description("부가정보"),
                    fieldWithPath("count").type(JsonFieldType.NUMBER).description("결과 개수"),
                    fieldWithPath("pageNumber").type(JsonFieldType.NUMBER).description("현재 페이지"),
                    fieldWithPath("totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"),
                    fieldWithPath("totalElements").type(JsonFieldType.NUMBER).description("전체 개수"),
                    fieldWithPath("data[].postId").type(JsonFieldType.NUMBER).description("게시글 ID"),
                    fieldWithPath("data[].memberId").type(JsonFieldType.NUMBER).description("회원 ID"),
                    fieldWithPath("data[].nickname").type(JsonFieldType.STRING).description("작성자 닉네임"),
                    fieldWithPath("data[].title").type(JsonFieldType.STRING).description("게시글 제목"),
                    fieldWithPath("data[].views").type(JsonFieldType.NUMBER).description("게시글 조회수"),
                    fieldWithPath("data[].votes").type(JsonFieldType.NUMBER).description("게시글 투표수"),
                    fieldWithPath("data[].solved").type(JsonFieldType.BOOLEAN).description("해결됨")
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