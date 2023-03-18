package com.practice.smallcommunity.member.interfaces;

import static com.practice.smallcommunity.testutils.interfaces.RestDocsHelper.ConstrainedFields;
import static com.practice.smallcommunity.testutils.interfaces.RestDocsHelper.generateDocument;
import static com.practice.smallcommunity.testutils.interfaces.RestDocsHelper.getConstrainedFields;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.smallcommunity.auth.application.OAuth2LoginService;
import com.practice.smallcommunity.auth.application.OAuth2RegistrationTokenService;
import com.practice.smallcommunity.auth.domain.oauth2.OAuth2Platform;
import com.practice.smallcommunity.auth.domain.oauth2.OAuth2RegistrationToken;
import com.practice.smallcommunity.testutils.interfaces.RestTest;
import com.practice.smallcommunity.member.interfaces.dto.OAuth2RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@RestTest
@WebMvcTest(OAuth2RegisterController.class)
class OAuth2RegisterControllerTest {

    @MockBean
    OAuth2RegistrationTokenService oAuth2RegistrationTokenService;

    @MockBean
    OAuth2LoginService oauth2LoginService;

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void 회원가입() throws Exception {
        //given
        String registrationKey = "registration-key";
        OAuth2RegistrationToken registrationToken = OAuth2RegistrationToken.builder()
            .email("test@mail.com")
            .key(registrationKey)
            .username("test")
            .platform(OAuth2Platform.GOOGLE)
            .build();
        when(oAuth2RegistrationTokenService.findOne("test@mail.com", registrationKey)).thenReturn(registrationToken);

        //when
        OAuth2RegisterRequest dto = OAuth2RegisterRequest.builder()
            .email("test@mail.com")
            .key(registrationKey)
            .nickname("tester")
            .build();
        ResultActions result = mvc.perform(post("/api/v1/oauth2")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)));

        //then
        ConstrainedFields fields = getConstrainedFields(OAuth2RegisterRequest.class);
        result.andExpect(status().isCreated())
            .andDo(generateDocument("oauth2",
                requestFields(
                    fields.withPath("email").type(JsonFieldType.STRING).description("가입 이메일"),
                    fields.withPath("key").type(JsonFieldType.STRING).description("OAuth2 가입용 토큰"),
                    fields.withPath("nickname").type(JsonFieldType.STRING).description("닉네임")
                )));
    }
}