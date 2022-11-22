package com.practice.smallcommunity.interfaces.member;

import static com.practice.smallcommunity.interfaces.RestDocsHelper.ConstrainedFields;
import static com.practice.smallcommunity.interfaces.RestDocsHelper.generateDocument;
import static com.practice.smallcommunity.interfaces.RestDocsHelper.getConstrainedFields;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.smallcommunity.application.auth.OAuth2LoginService;
import com.practice.smallcommunity.application.auth.OAuth2RegistrationTokenService;
import com.practice.smallcommunity.domain.auth.oauth2.OAuth2Platform;
import com.practice.smallcommunity.domain.auth.oauth2.OAuth2RegistrationToken;
import com.practice.smallcommunity.interfaces.RestTest;
import com.practice.smallcommunity.interfaces.member.dto.OAuth2RegisterRequest;
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
            .key(registrationKey)
            .email("test@mail.com")
            .username("test")
            .platform(OAuth2Platform.GOOGLE)
            .build();
        when(oAuth2RegistrationTokenService.findByKey(registrationKey)).thenReturn(registrationToken);

        //when
        OAuth2RegisterRequest dto = OAuth2RegisterRequest.builder()
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
                    fields.withPath("key").type(JsonFieldType.STRING).description("OAuth2 가입용 토큰"),
                    fields.withPath("nickname").type(JsonFieldType.STRING).description("닉네임")
                )));
    }
}