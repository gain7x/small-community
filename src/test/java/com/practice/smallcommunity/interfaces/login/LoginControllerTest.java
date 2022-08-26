package com.practice.smallcommunity.interfaces.login;

import static com.practice.smallcommunity.interfaces.RestDocsHelper.generateDocument;
import static com.practice.smallcommunity.interfaces.RestDocsHelper.getConstrainedFields;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.smallcommunity.interfaces.RestDocsHelper.ConstrainedFields;
import com.practice.smallcommunity.interfaces.login.dto.LoginRequest;
import com.practice.smallcommunity.application.LoginTokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@AutoConfigureRestDocs
@WebMvcTest(LoginController.class)
class LoginControllerTest {

    @MockBean
    LoginTokenService loginTokenService;

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void 로그인() throws Exception {
        //given
        String expected = "jwt-token";

        when(loginTokenService.issuance("userA", "password"))
            .thenReturn(expected);

        LoginRequest request = new LoginRequest("userA", "password");

        //when
        ResultActions result = mvc.perform(post("/api/v1/auth")
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .with(csrf()));

        //then
        ConstrainedFields fields = getConstrainedFields(LoginRequest.class);

        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken", expected).exists())
            .andDo(generateDocument("login",
                requestFields(
                    fields.withPath("username").description("회원 아이디"),
                    fields.withPath("password").description("비밀번호")
                ), responseFields(
                    fieldWithPath("accessToken").description("인증 토큰")
                )));
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        SecurityFilterChain web(HttpSecurity http) throws Exception {

            http
                .csrf().disable()
                .httpBasic().disable()
                .formLogin().disable()
                .authorizeRequests().anyRequest().permitAll();

            return http.build();
        }
    }
}