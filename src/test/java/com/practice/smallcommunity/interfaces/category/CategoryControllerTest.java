package com.practice.smallcommunity.interfaces.category;

import static com.practice.smallcommunity.interfaces.RestDocsHelper.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.smallcommunity.application.CategoryService;
import com.practice.smallcommunity.domain.category.Category;
import com.practice.smallcommunity.interfaces.category.dto.CategoryRequest;
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
@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @MockBean
    CategoryService categoryService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mvc;

    Category dummy = DomainGenerator.createCategory("개발");

    @Test
    @WithMockUser
    void 카테고리등록() throws Exception {
        //given
        when(categoryService.register(any(Category.class)))
            .thenReturn(dummy);

        CategoryRequest dto = new CategoryRequest("개발", true);

        //when
        ResultActions result = mvc.perform(post("/api/v1/categories")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto))
            .accept(MediaType.APPLICATION_JSON)
            .with(csrf()));

        ConstrainedFields fields = getConstrainedFields(CategoryRequest.class);

        //then
        result.andExpect(status().isCreated())
            .andDo(generateDocument("category", requestFields(
                fields.withPath("name").type(JsonFieldType.STRING).description("카테고리명"),
                fields.withPath("enable").type(JsonFieldType.BOOLEAN).description("사용여부")
            )));
    }

    @Test
    @WithMockUser
    void 카테고리조회() throws Exception {
        //given
        when(categoryService.findOne(anyLong()))
            .thenReturn(dummy);

        //when
        ResultActions result = mvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/categories/{categoryId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf()));

        //then
        result.andExpect(status().isOk())
            .andDo(generateDocument("category",
                pathParameters(
                    parameterWithName("categoryId").description("카테고리 번호")
                ),
                responseFields(
                    fieldWithPath("name").type(JsonFieldType.STRING).description("카테고리명")
                )));
    }

    @Test
    @WithMockUser
    void 카테고리수정() throws Exception {
        //given
        when(categoryService.update(anyLong(), anyString(), anyBoolean()))
            .thenReturn(dummy);

        //when
        CategoryRequest dto = new CategoryRequest("개발", true);

        ResultActions result = mvc.perform(
            RestDocumentationRequestBuilders.patch("/api/v1/categories/{categoryId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf()));

        ConstrainedFields fields = getConstrainedFields(CategoryRequest.class);

        //then
        result.andExpect(status().isNoContent())
            .andDo(generateDocument("category",
                pathParameters(
                    parameterWithName("categoryId").description("카테고리 번호")
                ),
                requestFields(
                    fields.withPath("name").type(JsonFieldType.STRING).description("카테고리명"),
                    fields.withPath("enable").type(JsonFieldType.BOOLEAN).description("사용여부")
                )));
    }


    @Test
    @WithMockUser
    void 카테고리삭제() throws Exception {
        //when
        ResultActions result = mvc.perform(
            RestDocumentationRequestBuilders.delete("/api/v1/categories/{categoryId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf()));

        //then
        result.andExpect(status().isNoContent())
            .andDo(generateDocument("category",
                pathParameters(
                    parameterWithName("categoryId").description("카테고리 번호")
                )));
    }

    @TestConfiguration
     static class Config {

        @Bean
        CategoryMapper categoryMapper() {
            return new CategoryMapperImpl();
        }
    }
}