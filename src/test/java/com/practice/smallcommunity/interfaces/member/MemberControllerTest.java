package com.practice.smallcommunity.interfaces.member;

import static com.practice.smallcommunity.interfaces.RestDocsHelper.generateDocument;
import static com.practice.smallcommunity.interfaces.RestDocsHelper.getConstrainedFields;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.smallcommunity.interfaces.RestDocsHelper.ConstrainedFields;
import com.practice.smallcommunity.interfaces.member.dto.MemberRegisterRequest;
import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.application.MemberService;
import com.practice.smallcommunity.utils.DomainGenerator;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@AutoConfigureRestDocs
@WebMvcTest(MemberController.class)
class MemberControllerTest {

    @MockBean
    MemberService memberService;

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Spy
    Member targetMember = DomainGenerator.createMember("A");

    @BeforeEach
    void setUp() {
        when(targetMember.getId()).thenReturn(1L);
    }

    @Test
    @WithMockUser
    void 회원가입() throws Exception {
        //given
        when(memberService.registerMember(any(Member.class)))
            .thenReturn(targetMember);

        MemberRegisterRequest dto = MemberRegisterRequest.builder()
            .password("password")
            .email("userA@mail.com")
            .nickname("firstUser")
            .build();

        //when
        ResultActions result = mvc.perform(post("/api/v1/members")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto))
            .accept(MediaType.APPLICATION_JSON)
            .with(csrf()));

        //then
        ConstrainedFields fields = getConstrainedFields(MemberRegisterRequest.class);

        result.andExpect(status().isCreated())
            .andDo(generateDocument("members",
                requestFields(
                    fields.withPath("email").type(JsonFieldType.STRING).description("이메일"),
                    fields.withPath("password").type(JsonFieldType.STRING).description("비밀번호"),
                    fields.withPath("nickname").type(JsonFieldType.STRING).description("별명")
                )));
    }

    @Test
    @WithMockUser
    void 회원정보_조회() throws Exception {
        //given
        when(memberService.findByUserId(targetMember.getId()))
            .thenReturn(targetMember);

        //when
        ResultActions result = mvc.perform(
                RestDocumentationRequestBuilders.get("/api/v1/members/{userId}", targetMember.getId())
                    .accept(MediaType.APPLICATION_JSON)
                    .with(csrf()))
            .andExpect(status().isOk());

        //then
        result.andExpect(status().isOk())
            .andDo(generateDocument("members",
                pathParameters(
                    parameterWithName("userId").description("회원 번호")
                ),
                responseFields(
                    fieldWithPath("nickname").type(JsonFieldType.STRING).description("회원 별명")
                )));
    }

    @Test
    @WithMockUser
    void 회원_상세정보_조회() throws Exception {
        //given
        when(memberService.findByUserId(targetMember.getId()))
            .thenReturn(targetMember);

        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(targetMember.getId(), null, null));

        //when
        ResultActions result = mvc.perform(get("/api/v1/members/details")
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer jwt-token")
                .with(csrf()))
            .andExpect(status().isOk());

        //then
        result.andExpect(status().isOk())
            .andDo(generateDocument("members",
                requestHeaders(
                    headerWithName("Authorization").description("JWT 토큰")
                ),
                responseFields(
                    fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                    fieldWithPath("nickname").type(JsonFieldType.STRING).description("별명"),
                    fieldWithPath("lastPasswordChange").type(LocalDateTime.class.getName())
                        .description("마지막 비밀번호 변경일")
                )));
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        MemberMapper memberMapper() {
            return new MemberMapperImpl();
        }
    }
}