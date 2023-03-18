package com.practice.smallcommunity.common.interfaces;

import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.practice.smallcommunity.common.exception.BusinessException;
import com.practice.smallcommunity.common.exception.BusinessExceptions;
import com.practice.smallcommunity.common.exception.ErrorCode;
import com.practice.smallcommunity.testutils.interfaces.RestTest;
import com.practice.smallcommunity.testutils.interfaces.WithMockMember;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@RestTest
@WebMvcTest(TestController.class)
class RestErrorControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    TestController testController;

    @Test
    @WithMockMember
    void 정의되지_않은_예외_핸들링() throws Exception {
        doThrow(new Exception("")).when(testController).test();

        //when
        ResultActions result = mvc.perform(get("/test"));

        //then
        result.andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockMember
    void 정의되지_않은_런타임_예외_핸들링() throws Exception {
        //given
        doThrow(new RuntimeException("")).when(testController).test();

        //when
        ResultActions result = mvc.perform(get("/test"));

        //then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @WithMockMember
    void 비즈니스_예외_핸들링() throws Exception {
        //given
        doThrow(new BusinessException(ErrorCode.RUNTIME_ERROR)).when(testController).test();

        //when
        ResultActions result = mvc.perform(get("/test"));

        //then
        result.andExpect(status().is(ErrorCode.RUNTIME_ERROR.getResponseStatus().value()));
    }

    @Test
    @WithMockMember
    void 비즈니스_예외_목록_핸들링() throws Exception {
        //given
        BusinessExceptions businessExceptions = new BusinessExceptions(ErrorCode.RUNTIME_ERROR,
            List.of(new BusinessException(ErrorCode.RUNTIME_ERROR)));
        doThrow(businessExceptions).when(testController).test();

        //when
        ResultActions result = mvc.perform(get("/test"));

        //then
        result.andExpect(status().is(ErrorCode.RUNTIME_ERROR.getResponseStatus().value()));
    }

    @Test
    @WithMockMember
    void 유효하지_않은_메서드_파라미터_예외_핸들링() throws Exception {
        //given
        //when
        ResultActions result = mvc.perform(post("/test")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{ \"name\": \"\" }"));

        //then
        result.andExpect(status().is(ErrorCode.VALIDATION_ERROR.getResponseStatus().value()));
    }
}