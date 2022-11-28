package com.practice.smallcommunity.interfaces.member;

import static com.practice.smallcommunity.interfaces.RestDocsHelper.generateDocument;
import static com.practice.smallcommunity.interfaces.RestDocsHelper.getConstrainedFields;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.smallcommunity.application.auth.EmailVerificationService;
import com.practice.smallcommunity.application.auth.LoginService;
import com.practice.smallcommunity.domain.auth.EmailVerificationToken;
import com.practice.smallcommunity.domain.auth.Login;
import com.practice.smallcommunity.interfaces.RestDocsHelper.ConstrainedFields;
import com.practice.smallcommunity.interfaces.RestTest;
import com.practice.smallcommunity.interfaces.member.dto.EmailVerificationRequest;
import com.practice.smallcommunity.interfaces.member.dto.MemberRegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@RestTest
@WebMvcTest(RegisterController.class)
class RegisterControllerTest {

    @MockBean
    EmailVerificationService emailVerificationService;

    @MockBean
    LoginService loginService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mvc;

    @Test
    void 이메일_인증() throws Exception {
        //given
        String email = "test@mail.com";
        String key = "key";
        String redirectUri = "https://test.com";

        when(emailVerificationService.check(eq(email), eq(key)))
            .thenReturn(EmailVerificationToken.builder()
                .email(email)
                .key(key)
                .build());

        when(loginService.verifyEmail(email))
            .thenReturn(Login.builder().build());

        //when
        EmailVerificationRequest dto = EmailVerificationRequest.builder()
            .email(email)
            .key(key)
            .redirectUri(redirectUri)
            .build();

        ResultActions result = mvc.perform(post("/api/v1/members/verify")
            .queryParam("email", dto.getEmail())
            .queryParam("key", dto.getKey())
            .queryParam("redirectUri", dto.getRedirectUri())
            .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().is3xxRedirection())
            .andDo(generateDocument("member",
                requestParameters(
                    parameterWithName("email").description("인증 대상 이메일"),
                    parameterWithName("key").description("인증 키"),
                    parameterWithName("redirectUri").description("이메일 인증 후 리다이렉트되는 URI."
                            + " 인증 성공 시 인증된 이메일( verifiedEmail )을 파라미터로 전달."
                            + " 실패 시 오류 코드( error ) 전달"))
                ))
            .andExpect(redirectedUrl("https://test.com?verifiedEmail=test@mail.com"));
    }

    @Test
    void 회원가입() throws Exception {
        //given
        MemberRegisterRequest dto = MemberRegisterRequest.builder()
            .password("password")
            .email("userA@mail.com")
            .nickname("firstUser")
            .redirectUri("https://test.com")
            .build();

        //when
        ResultActions result = mvc.perform(post("/api/v1/members")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto))
            .accept(MediaType.APPLICATION_JSON));

        //then
        ConstrainedFields fields = getConstrainedFields(MemberRegisterRequest.class);

        result.andExpect(status().isCreated())
            .andDo(generateDocument("member",
                requestFields(
                    fields.withPath("email").type(JsonFieldType.STRING).description("이메일"),
                    fields.withPath("password").type(JsonFieldType.STRING).description("비밀번호"),
                    fields.withPath("nickname").type(JsonFieldType.STRING).description("별명"),
                    fields.withPath("redirectUri").type(JsonFieldType.STRING).description("이메일 인증 후 리다이렉트되는 URI")
                )));
    }
}