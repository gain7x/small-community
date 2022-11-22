package com.practice.smallcommunity.interfaces.member;

import static com.practice.smallcommunity.interfaces.RestDocsHelper.generateDocument;
import static com.practice.smallcommunity.interfaces.RestDocsHelper.getConstrainedFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.smallcommunity.application.auth.LoginService;
import com.practice.smallcommunity.application.auth.MailVerificationService;
import com.practice.smallcommunity.interfaces.RestDocsHelper.ConstrainedFields;
import com.practice.smallcommunity.interfaces.RestTest;
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
    MailVerificationService mailVerificationService;

    @MockBean
    LoginService loginService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mvc;

    @Test
    void 회원가입() throws Exception {
        //given
        MemberRegisterRequest dto = MemberRegisterRequest.builder()
            .password("password")
            .email("userA@mail.com")
            .nickname("firstUser")
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
                    fields.withPath("nickname").type(JsonFieldType.STRING).description("별명")
                )));
    }
}