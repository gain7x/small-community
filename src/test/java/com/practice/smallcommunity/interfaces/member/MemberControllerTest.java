package com.practice.smallcommunity.interfaces.member;

import static com.practice.smallcommunity.interfaces.RestDocsHelper.baseData;
import static com.practice.smallcommunity.interfaces.RestDocsHelper.generateDocument;
import static com.practice.smallcommunity.interfaces.RestDocsHelper.getConstrainedFields;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.smallcommunity.application.member.MemberService;
import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.interfaces.RestDocsHelper.ConstrainedFields;
import com.practice.smallcommunity.interfaces.RestTest;
import com.practice.smallcommunity.interfaces.WithMockMember;
import com.practice.smallcommunity.interfaces.member.dto.MemberPasswordChangeRequest;
import com.practice.smallcommunity.interfaces.member.dto.MemberUpdateRequest;
import com.practice.smallcommunity.utils.DomainGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@RestTest
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
    @WithMockMember
    void 회원정보_조회() throws Exception {
        //given
        when(memberService.findByUserId(targetMember.getId()))
            .thenReturn(targetMember);

        //when
        ResultActions result = mvc.perform(
                RestDocumentationRequestBuilders.get("/api/v1/members/{userId}", targetMember.getId())
                    .accept(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk())
            .andDo(generateDocument("member",
                pathParameters(
                    parameterWithName("userId").description("회원 번호")
                ),
                responseFields(
                    baseData(),
                    fieldWithPath("nickname").type(JsonFieldType.STRING).description("회원 별명")
                )));
    }

    @Test
    @WithMockMember
    void 회원정보_수정() throws Exception {
        //given
        String newNickname = "newNickname";
        targetMember.changeNickname(newNickname);

        when(memberService.update(1L, newNickname))
            .thenReturn(targetMember);

        //when
        MemberUpdateRequest dto = MemberUpdateRequest.builder()
            .nickname(newNickname)
            .build();

        ResultActions result = mvc.perform(patch("/api/v1/members")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)));

        //then
        ConstrainedFields fields = getConstrainedFields(MemberUpdateRequest.class);

        result.andExpect(status().isNoContent())
            .andDo(generateDocument("member",
                requestFields(
                    fields.withPath("nickname").type(JsonFieldType.STRING).description("새로운 별명")
                )));
    }

    @Test
    @WithMockMember
    void 회원_암호_변경() throws Exception {
        //given
        String currentPassword = "currentPassword";
        String newPassword = "newPassword";

        when(memberService.changePassword(1L, currentPassword, newPassword))
            .thenReturn(targetMember);

        //when
        MemberPasswordChangeRequest dto = MemberPasswordChangeRequest.builder()
            .currentPassword(currentPassword)
            .newPassword(newPassword)
            .build();

        ResultActions result = mvc.perform(patch("/api/v1/members/password")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)));

        //then
        ConstrainedFields fields = getConstrainedFields(
            MemberPasswordChangeRequest.class);

        result.andExpect(status().isNoContent())
            .andDo(generateDocument("member",
                requestFields(
                    fields.withPath("currentPassword").type(JsonFieldType.STRING).description("기존 비밀번호"),
                    fields.withPath("newPassword").type(JsonFieldType.STRING).description("새로운 비밀번호")
                )));
    }

    @Test
    @WithMockMember
    void 회원탈퇴() throws Exception {
        //given
        when(memberService.withdrawal(1L))
            .thenReturn(targetMember);

        //when
        ResultActions result = mvc.perform(delete("/api/v1/members")
            .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isNoContent())
            .andDo(generateDocument("member"));
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        MemberMapper memberMapper() {
            return new MemberMapperImpl();
        }
    }
}