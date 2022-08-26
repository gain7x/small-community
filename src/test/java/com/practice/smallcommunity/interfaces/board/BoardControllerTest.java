package com.practice.smallcommunity.interfaces.board;

import static com.practice.smallcommunity.interfaces.RestDocsHelper.ConstrainedFields;
import static com.practice.smallcommunity.interfaces.RestDocsHelper.generateDocument;
import static com.practice.smallcommunity.interfaces.RestDocsHelper.getConstrainedFields;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.smallcommunity.application.BoardService;
import com.practice.smallcommunity.application.CategoryService;
import com.practice.smallcommunity.application.dto.BoardDto;
import com.practice.smallcommunity.domain.board.Board;
import com.practice.smallcommunity.domain.category.Category;
import com.practice.smallcommunity.interfaces.board.dto.BoardRequest;
import com.practice.smallcommunity.utils.DomainGenerator;
import org.junit.jupiter.api.BeforeEach;
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
@WebMvcTest(BoardController.class)
class BoardControllerTest {

    @MockBean
    CategoryService categoryService;

    @MockBean
    BoardService boardService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mvc;

    Category category = DomainGenerator.createCategory("개발");
    Board dummyBoard = DomainGenerator.createBoard(category, "Java");

    @BeforeEach
    void setUp() {
        when(categoryService.findOne(1L))
            .thenReturn(category);
    }

    @Test
    @WithMockUser
    void 게시판등록() throws Exception {
        //given
        when(boardService.register(any(BoardDto.class)))
            .thenReturn(dummyBoard);

        //when
        BoardRequest dto = new BoardRequest(1L, "Java", true);

        ResultActions result = mvc.perform(post("/api/v1/boards")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto))
            .accept(MediaType.APPLICATION_JSON)
            .with(csrf()));

        //then
        ConstrainedFields fields = getConstrainedFields(BoardRequest.class);

        result.andExpect(status().isCreated())
            .andDo(generateDocument("board", requestFields(
                fields.withPath("categoryId").type(JsonFieldType.NUMBER).description("소속 카테고리 번호"),
                fields.withPath("name").type(JsonFieldType.STRING).description("게시판 이름"),
                fields.withPath("enable").type(JsonFieldType.BOOLEAN).description("사용여부")
            )));
    }

    @Test
    @WithMockUser
    void 게시판조회() throws Exception {
        //given
        when(boardService.findOne(1L))
            .thenReturn(dummyBoard);

        //when
        ResultActions result = mvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/boards/{boardId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf()));

        //then
        result.andExpect(status().isOk())
            .andDo(generateDocument("board",
                pathParameters(
                    parameterWithName("boardId").description("게시판 번호")
                ),
                responseFields(
                    fieldWithPath("name").type(JsonFieldType.STRING).description("게시판 이름")
                )));
    }

    @Test
    @WithMockUser
    void 게시판수정() throws Exception {
        //given
        when(boardService.update(eq(1L), any(BoardDto.class)))
            .thenReturn(dummyBoard);

        //when
        BoardRequest dto = new BoardRequest(1L, "Java", true);

        ResultActions result = mvc.perform(
            RestDocumentationRequestBuilders.patch("/api/v1/boards/{boardId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf()));

        //then
        ConstrainedFields fields = getConstrainedFields(BoardRequest.class);

        result.andExpect(status().isNoContent())
            .andDo(generateDocument("board",
                pathParameters(
                    parameterWithName("boardId").description("게시판 번호")
                ),
                requestFields(
                    fields.withPath("categoryId").type(JsonFieldType.NUMBER).description("소속 카테고리 번호"),
                    fields.withPath("name").type(JsonFieldType.STRING).description("게시판 이름"),
                    fields.withPath("enable").type(JsonFieldType.BOOLEAN).description("사용여부")
                )));
    }

    @Test
    @WithMockUser
    void 게시판삭제() throws Exception {
        //when
        ResultActions result = mvc.perform(
            RestDocumentationRequestBuilders.delete("/api/v1/boards/{boardId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf()));

        //then
        result.andExpect(status().isNoContent())
            .andDo(generateDocument("board",
                pathParameters(
                    parameterWithName("boardId").description("게시판 번호")
                )));
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        BoardMapper boardMapper() {
            return new BoardMapperImpl();
        }
    }
}