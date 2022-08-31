package com.practice.smallcommunity.interfaces.login;

import static com.practice.smallcommunity.interfaces.RestDocsHelper.generateDocument;
import static com.practice.smallcommunity.interfaces.RestDocsHelper.getConstrainedFields;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.smallcommunity.application.LoginService;
import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.interfaces.RestDocsHelper.ConstrainedFields;
import com.practice.smallcommunity.interfaces.RestTest;
import com.practice.smallcommunity.interfaces.login.dto.LoginRequest;
import com.practice.smallcommunity.security.JwtTokenProvider;
import com.practice.smallcommunity.utils.DomainGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@RestTest
@WebMvcTest(LoginController.class)
class LoginControllerTest {

    @MockBean
    LoginService loginTokenService;

    @MockBean
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    Member dummyMember = DomainGenerator.createMember("A");

    @BeforeEach
    void setUp() {
        when(jwtTokenProvider.createToken(dummyMember))
            .thenReturn("jwt-token");
    }

    @Test
    void 로그인() throws Exception {
        //given
        String expected = "jwt-token";

        when(loginTokenService.login(dummyMember.getEmail(), dummyMember.getPassword()))
            .thenReturn(dummyMember);

        LoginRequest request = new LoginRequest(dummyMember.getEmail(), dummyMember.getPassword());

        //when
        ResultActions result = mvc.perform(post("/api/v1/auth")
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON));

        //then
        ConstrainedFields fields = getConstrainedFields(LoginRequest.class);

        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken", expected).exists())
            .andDo(generateDocument("login",
                requestFields(
                    fields.withPath("email").description("회원 아이디"),
                    fields.withPath("password").description("비밀번호")
                ), responseFields(
                    fieldWithPath("accessToken").description("인증 토큰"),
                    fieldWithPath("email").description("이메일"),
                    fieldWithPath("nickname").description("별명"),
                    fieldWithPath("lastPasswordChange").description("마지막 비밀번호 변경일")
                )));
    }
}