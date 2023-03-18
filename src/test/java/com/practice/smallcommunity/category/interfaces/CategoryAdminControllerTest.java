package com.practice.smallcommunity.category.interfaces;

import static com.practice.smallcommunity.testutils.interfaces.RestDocsHelper.baseData;
import static com.practice.smallcommunity.testutils.interfaces.RestDocsHelper.collectionData;
import static com.practice.smallcommunity.testutils.interfaces.RestDocsHelper.generateDocument;
import static com.practice.smallcommunity.testutils.interfaces.RestDocsHelper.getConstrainedFields;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.smallcommunity.category.CategoryService;
import com.practice.smallcommunity.category.interfaces.dto.CategoryRequest;
import com.practice.smallcommunity.category.Category;
import com.practice.smallcommunity.testutils.interfaces.RestDocsHelper.ConstrainedFields;
import com.practice.smallcommunity.testutils.interfaces.RestTest;
import com.practice.smallcommunity.testutils.interfaces.WithMockMember;
import com.practice.smallcommunity.testutils.DomainGenerator;
import java.util.List;
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
@WebMvcTest(CategoryAdminController.class)
class CategoryAdminControllerTest {

    @MockBean
    CategoryService categoryService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mvc;

    Category category = DomainGenerator.createCategory("test", "테스트");

    @Test
    @WithMockMember(roles = "ADMIN")
    void 카테고리등록() throws Exception {
        //given
        when(categoryService.register(any(Category.class)))
            .thenReturn(category);

        CategoryRequest dto = new CategoryRequest("dev", "개발", true, false);

        //when
        ResultActions result = mvc.perform(post("/api/admin/categories")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto))
            .accept(MediaType.APPLICATION_JSON));

        ConstrainedFields fields = getConstrainedFields(CategoryRequest.class);

        //then
        result.andExpect(status().isCreated())
            .andDo(generateDocument("admin/category", requestFields(
                fields.withPath("code").type(JsonFieldType.STRING).description("카테고리 코드"),
                fields.withPath("name").type(JsonFieldType.STRING).description("카테고리 이름"),
                fields.withPath("enable").type(JsonFieldType.BOOLEAN).description("사용 여부"),
                fields.withPath("cudAdminOnly").type(JsonFieldType.BOOLEAN).description("관리자 전용 여부")
            )));
    }

    @Test
    @WithMockMember(roles = "ADMIN")
    void 전체_카테고리조회() throws Exception {
        //given
        when(categoryService.findAll())
            .thenReturn(List.of(category));

        //when
        ResultActions result = mvc.perform(
            RestDocumentationRequestBuilders.get("/api/admin/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk())
            .andDo(generateDocument("admin/category",
                responseFields(
                    collectionData(),
                    fieldWithPath("code").type(JsonFieldType.STRING).description("카테고리 코드"),
                    fieldWithPath("name").type(JsonFieldType.STRING).description("카테고리 이름"),
                    fieldWithPath("enable").type(JsonFieldType.BOOLEAN).description("사용 여부"),
                    fieldWithPath("cudAdminOnly").type(JsonFieldType.BOOLEAN).description("관리자 전용 여부")
                )));
    }

    @Test
    @WithMockMember(roles = "ADMIN")
    void 카테고리조회() throws Exception {
        //given
        when(categoryService.findOne(anyLong()))
            .thenReturn(category);

        //when
        ResultActions result = mvc.perform(
            RestDocumentationRequestBuilders.get("/api/admin/categories/{categoryId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk())
            .andDo(generateDocument("admin/category",
                pathParameters(
                    parameterWithName("categoryId").description("카테고리 번호")
                ),
                responseFields(
                    baseData(),
                    fieldWithPath("code").type(JsonFieldType.STRING).description("카테고리 코드"),
                    fieldWithPath("name").type(JsonFieldType.STRING).description("카테고리 이름"),
                    fieldWithPath("enable").type(JsonFieldType.BOOLEAN).description("사용 여부"),
                    fieldWithPath("cudAdminOnly").type(JsonFieldType.BOOLEAN).description("관리자 전용 여부")
                )));
    }

    @Test
    @WithMockMember(roles = "ADMIN")
    void 카테고리수정() throws Exception {
        //given
        when(categoryService.update(eq("test"), any()))
            .thenReturn(category);

        //when
        CategoryRequest dto = new CategoryRequest("tech", "기술", true, false);

        ResultActions result = mvc.perform(
            RestDocumentationRequestBuilders.patch("/api/admin/categories/{categoryCode}", "test")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .accept(MediaType.APPLICATION_JSON));

        ConstrainedFields fields = getConstrainedFields(CategoryRequest.class);

        //then
        result.andExpect(status().isNoContent())
            .andDo(generateDocument("admin/category",
                pathParameters(
                    parameterWithName("categoryCode").description("카테고리 코드")
                ),
                requestFields(
                    fields.withPath("code").type(JsonFieldType.STRING).description("카테고리 코드"),
                    fields.withPath("name").type(JsonFieldType.STRING).description("카테고리 이름"),
                    fields.withPath("enable").type(JsonFieldType.BOOLEAN).description("사용 여부"),
                    fields.withPath("cudAdminOnly").type(JsonFieldType.BOOLEAN).description("관리자 전용 여부")
                )));
    }

    @Test
    @WithMockMember(roles = "ADMIN")
    void 카테고리삭제() throws Exception {
        //when
        ResultActions result = mvc.perform(
            RestDocumentationRequestBuilders.delete("/api/admin/categories/{categoryCode}", "test")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isNoContent())
            .andDo(generateDocument("admin/category",
                pathParameters(
                    parameterWithName("categoryCode").description("카테고리 코드")
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