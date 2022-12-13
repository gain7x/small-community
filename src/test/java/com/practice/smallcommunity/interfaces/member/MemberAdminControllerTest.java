package com.practice.smallcommunity.interfaces.member;

import static com.practice.smallcommunity.interfaces.RestDocsHelper.generateDocument;
import static com.practice.smallcommunity.interfaces.RestDocsHelper.getConstrainedFields;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.smallcommunity.application.auth.LoginService;
import com.practice.smallcommunity.application.member.MemberService;
import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.interfaces.RestDocsHelper.ConstrainedFields;
import com.practice.smallcommunity.interfaces.RestTest;
import com.practice.smallcommunity.interfaces.WithMockMember;
import com.practice.smallcommunity.interfaces.member.dto.MemberRegisterRequest;
import com.practice.smallcommunity.interfaces.member.dto.MemberUpdateRequest;
import com.practice.smallcommunity.utils.DomainGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@RestTest
@WebMvcTest(MemberAdminController.class)
class MemberAdminControllerTest {

    @MockBean
    MemberService memberService;

    @MockBean
    LoginService loginService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mvc;

    @Spy
    Member member = DomainGenerator.createMember("A");

    @BeforeEach
    void setUp() {
        when(member.getId()).thenReturn(1L);
    }

    @Test
    @WithMockMember(roles = "ADMIN")
    void 회원등록() throws Exception {
        //given
        MemberRegisterRequest dto = MemberRegisterRequest.builder()
            .password("password")
            .email("userA@mail.com")
            .nickname("firstUser")
            .redirectUri("https://test.com")
            .build();

        //when
        ResultActions result = mvc.perform(post("/api/admin/members")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto))
            .accept(MediaType.APPLICATION_JSON));

        //then
        ConstrainedFields fields = getConstrainedFields(MemberRegisterRequest.class);

        result.andExpect(status().isCreated())
            .andDo(generateDocument("admin/member",
                requestFields(
                    fields.withPath("email").type(JsonFieldType.STRING).description("이메일"),
                    fields.withPath("password").type(JsonFieldType.STRING).description("비밀번호"),
                    fields.withPath("nickname").type(JsonFieldType.STRING).description("별명"),
                    fields.withPath("redirectUri").type(JsonFieldType.STRING)
                        .description("관리자가 추가하는 회원은 이메일 인증 상태로 등록되므로 미사용 필드. 단, 빈 문자열이면 안됩니다.")
                )));
    }

    @Test
    @WithMockMember(roles = "ADMIN")
    void 회원정보_수정() throws Exception {
        //given
        String newNickname = "newNickname";
        member.changeNickname(newNickname);

        when(memberService.update(1L, newNickname))
            .thenReturn(member);

        //when
        MemberUpdateRequest dto = MemberUpdateRequest.builder()
            .nickname(newNickname)
            .build();

        ResultActions result = mvc.perform(
            RestDocumentationRequestBuilders.patch("/api/admin/members/{memberId}", 1L)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)));

        //then
        ConstrainedFields fields = getConstrainedFields(MemberUpdateRequest.class);

        result.andExpect(status().isNoContent())
            .andDo(generateDocument("admin/member",
                pathParameters(
                    parameterWithName("memberId").description("수정할 회원의 번호")
                ),
                requestFields(
                    fields.withPath("nickname").type(JsonFieldType.STRING).description("새로운 별명")
                )));
    }

    @Test
    @WithMockMember(roles = "ADMIN")
    void 회원_강제_탈퇴() throws Exception {
        //given
        when(memberService.withdrawal(1L))
            .thenReturn(member);

        //when
        ResultActions result = mvc.perform(
            RestDocumentationRequestBuilders.delete("/api/admin/members/{memberId}", 1L)
            .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isNoContent())
            .andDo(generateDocument("admin/member",
                pathParameters(
                    parameterWithName("memberId").description("탈퇴시킬 회원의 번호")
                ))
            );
    }
}