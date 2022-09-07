package com.practice.smallcommunity.interfaces.login;

import static com.practice.smallcommunity.interfaces.RestDocsHelper.baseData;
import static com.practice.smallcommunity.interfaces.RestDocsHelper.generateDocument;
import static com.practice.smallcommunity.interfaces.RestDocsHelper.getConstrainedFields;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.smallcommunity.application.LoginService;
import com.practice.smallcommunity.application.dto.LoginDto;
import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.interfaces.RestDocsHelper.ConstrainedFields;
import com.practice.smallcommunity.interfaces.RestTest;
import com.practice.smallcommunity.interfaces.login.dto.LoginRequest;
import com.practice.smallcommunity.interfaces.login.dto.RefreshRequest;
import com.practice.smallcommunity.utils.DomainGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@RestTest
@WebMvcTest(LoginController.class)
class LoginControllerTest {

    @MockBean
    LoginService loginService;

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    Member dummyMember = DomainGenerator.createMember("A");
    LoginDto dummyLoginDto = LoginDto.builder()
        .accessToken("access-token")
        .refreshToken("refresh-token")
        .member(dummyMember)
        .build();

    @Test
    void 로그인() throws Exception {
        //given
        when(loginService.login(dummyMember.getEmail(), dummyMember.getPassword()))
            .thenReturn(dummyLoginDto);

        LoginRequest request = new LoginRequest(dummyMember.getEmail(), dummyMember.getPassword());

        //when
        ResultActions result = mvc.perform(post("/api/v1/auth")
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON));

        //then
        ConstrainedFields fields = getConstrainedFields(LoginRequest.class);

        result.andExpect(status().isOk())
            .andDo(generateDocument("auth",
                requestFields(
                    fields.withPath("email").description("회원 아이디"),
                    fields.withPath("password").description("비밀번호")
                ), responseFields(
                    baseData(),
                    fieldWithPath("accessToken").type(JsonFieldType.STRING).description("액세스 토큰"),
                    fieldWithPath("refreshToken").type(JsonFieldType.STRING).description("리프레시 토큰"),
                    fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                    fieldWithPath("nickname").type(JsonFieldType.STRING).description("별명"),
                    fieldWithPath("lastPasswordChange").type(JsonFieldType.STRING)
                        .description("마지막 비밀번호 변경일")
                )));
    }

    @Test
    void 토큰_새로고침() throws Exception {
        //given
        when(loginService.refresh("access-token", "refresh-token"))
            .thenReturn(dummyLoginDto);

        //when
        RefreshRequest dto = RefreshRequest.builder()
            .accessToken("access-token")
            .refreshToken("refresh-token")
            .build();

        ResultActions result = mvc.perform(post("/api/v1/auth/refresh")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto))
            .accept(MediaType.APPLICATION_JSON));

        //then
        ConstrainedFields fields = getConstrainedFields(RefreshRequest.class);

        result.andExpect(status().isOk())
            .andDo(generateDocument("auth",
                requestFields(
                    fields.withPath("accessToken").type(JsonFieldType.STRING).description("액세스 토큰"),
                    fields.withPath("refreshToken").type(JsonFieldType.STRING)
                        .description("리프레시 토큰")
                ), responseFields(
                    baseData(),
                    fieldWithPath("accessToken").type(JsonFieldType.STRING).description("액세스 토큰"),
                    fieldWithPath("refreshToken").type(JsonFieldType.STRING).description("리프레시 토큰"),
                    fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                    fieldWithPath("nickname").type(JsonFieldType.STRING).description("별명"),
                    fieldWithPath("lastPasswordChange").type(JsonFieldType.STRING)
                        .description("마지막 비밀번호 변경일")
                )));
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        LoginMapper loginMapper() {
            return new LoginMapperImpl();
        }
    }
}