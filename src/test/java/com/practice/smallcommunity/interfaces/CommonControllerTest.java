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
                fieldWithPath("data[]").type(JsonFieldType.ARRAY).description("응답 데이터"),
                fieldWithPath("count").type(JsonFieldType.NUMBER).description("응답 데이터 개수"),
                fieldWithPath("pageNumber").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                fieldWithPath("totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 개수"),
                fieldWithPath("totalElements").type(JsonFieldType.NUMBER).description("전체 데이터 개수")
            )));
    }

    @Test
    @WithMockMember
    void 예외_응답() throws Exception {
        //when
        ResultActions result = mvc.perform(get("/docs/error")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isBadRequest())
            .andDo(generateDocument("common", responseFields(
                fieldWithPath("code").type(JsonFieldType.STRING).description("오류 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING).description("오류 메시지"),
                fieldWithPath("errors").type(JsonFieldType.ARRAY).optional().description("오류 추가 정보"),
                fieldWithPath("errors[].field").type(JsonFieldType.STRING).description("오류 관련 필드"),
                fieldWithPath("errors[].reason").type(JsonFieldType.STRING).description("오류 발생 이유")
            )));
    }

    @Test
    @WithMockMember
    void 예외_목록() throws Exception{
        //when
        ResultActions result = mvc.perform(get("/docs/errorCodes")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk())
            .andDo(generateDocument("common"));
    }
}
