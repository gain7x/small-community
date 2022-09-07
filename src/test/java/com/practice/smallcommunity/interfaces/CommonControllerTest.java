package com.practice.smallcommunity.interfaces;

import static com.practice.smallcommunity.interfaces.RestDocsHelper.generateDocument;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@RestTest
@WebMvcTest(CommonController.class)
public class CommonControllerTest {

    @Autowired
    MockMvc mvc;

    @Test
    @WithMockMember
    void 공통_응답_기본() throws Exception {
        //when
        ResultActions result = mvc.perform(get("/docs/base")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk())
            .andDo(generateDocument("common", responseFields(
                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                fieldWithPath("reason").type(JsonFieldType.STRING).description("응답 이유"),
                fieldWithPath("data").type(JsonFieldType.STRING).description("응답 데이터")
            )));
    }

    @Test
    @WithMockMember
    void 공통_응답_컬렉션() throws Exception {
        //when
        ResultActions result = mvc.perform(get("/docs/collection")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk())
            .andDo(generateDocument("common", responseFields(
                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                fieldWithPath("reason").type(JsonFieldType.STRING).description("응답 이유"),
                fieldWithPath("data[]").type(JsonFieldType.ARRAY).description("응답 데이터"),
                fieldWithPath("count").type(JsonFieldType.NUMBER).description("응답 데이터 개수")
            )));
    }

    @Test
    @WithMockMember
    void 공통_응답_페이지() throws Exception {
        //when
        ResultActions result = mvc.perform(get("/docs/page")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk())
            .andDo(generateDocument("common", responseFields(
                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                fieldWithPath("reason").type(JsonFieldType.STRING).description("응답 이유"),
                fieldWithPath("data[]").type(JsonFieldType.ARRAY).description("응답 데이터"),
                fieldWithPath("count").type(JsonFieldType.NUMBER).description("응답 데이터 개수"),
                fieldWithPath("pageNumber").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                fieldWithPath("totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 개수"),
                fieldWithPath("totalElements").type(JsonFieldType.NUMBER).description("전체 데이터 개수")
            )));
    }
}
