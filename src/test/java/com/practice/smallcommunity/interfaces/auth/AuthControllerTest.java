package com.practice.smallcommunity.interfaces.auth;

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
import com.practice.smallcommunity.application.auth.AuthService;
import com.practice.smallcommunity.application.auth.dto.AuthDto;
import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.interfaces.RestDocsHelper.ConstrainedFields;
import com.practice.smallcommunity.interfaces.RestTest;
import com.practice.smallcommunity.interfaces.auth.dto.LoginRequest;
import com.practice.smallcommunity.utils.DomainGenerator;
import java.sql.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import javax.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
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
@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @MockBean
    AuthService authService;

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Spy
    Member dummyMember = DomainGenerator.createMember("A");

    AuthDto dummyAuthDto;

    @BeforeEach
    void setUp() {
        when(dummyMember.getId()).thenReturn(1L);
        dummyAuthDto = AuthDto.builder()
            .accessToken("access-token")
            .accessTokenExpires(Date.from(Instant.now().plus(1, ChronoUnit.MINUTES)))
            .refreshToken("refresh-token")
            .refreshTokenExpires(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
            .member(dummyMember)
            .build();
    }

    @Test
    void 로그인() throws Exception {
        //given
        when(authService.login(dummyMember.getEmail(), dummyMember.getPassword()))
            .thenReturn(dummyAuthDto);

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
                    fieldWithPath("accessTokenExpires").type(JsonFieldType.NUMBER)
                        .description("액세스 토큰이 만료되는 시간( ms )"),
                    fieldWithPath("refreshToken").type(JsonFieldType.STRING).description("리프레시 토큰"),
                    fieldWithPath("refreshTokenExpires").type(JsonFieldType.NUMBER)
                        .description("리프레시 토큰이 만료되는 시간( ms )"),
                    fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("회원 번호"),
                    fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                    fieldWithPath("nickname").type(JsonFieldType.STRING).description("별명"),
                    fieldWithPath("lastPasswordChange").type(JsonFieldType.STRING)
                        .description("마지막 비밀번호 변경일"),
                    fieldWithPath("admin").type(JsonFieldType.BOOLEAN).optional()
                        .description("로그인 회원이 관리자인 경우에만 포함, 값은 TRUE 고정")
                )));
    }

    @Test
    void 토큰_새로고침() throws Exception {
        //given
        when(authService.refresh("some-refresh-token"))
            .thenReturn(dummyAuthDto);

        //when
        ResultActions result = mvc.perform(post("/api/v1/auth/refresh")
            .contentType(MediaType.APPLICATION_JSON)
            .cookie(new Cookie("refresh_token", "some-refresh-token"))
            .accept(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk())
            .andDo(generateDocument("auth",
                responseFields(
                    baseData(),
                    fieldWithPath("accessToken").type(JsonFieldType.STRING).description("액세스 토큰"),
                    fieldWithPath("accessTokenExpires").type(JsonFieldType.NUMBER)
                        .description("액세스 토큰이 만료되는 시간( ms )"),
                    fieldWithPath("refreshToken").type(JsonFieldType.STRING).description("리프레시 토큰"),
                    fieldWithPath("refreshTokenExpires").type(JsonFieldType.NUMBER)
                        .description("리프레시 토큰이 만료되는 시간( ms )"),
                    fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("회원 번호"),
                    fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                    fieldWithPath("nickname").type(JsonFieldType.STRING).description("별명"),
                    fieldWithPath("lastPasswordChange").type(JsonFieldType.STRING)
                        .description("마지막 비밀번호 변경일")
                )));
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        AuthMapper loginMapper() {
            return new AuthMapperImpl();
        }
    }
}