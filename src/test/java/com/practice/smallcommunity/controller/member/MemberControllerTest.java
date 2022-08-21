package com.practice.smallcommunity.controller.member;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.smallcommunity.controller.member.dto.MemberRegisterDto;
import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.service.member.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@WebMvcTest(MemberController.class)
class MemberControllerTest {

    @MockBean
    MemberService memberService;

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void 회원가입() throws Exception {
        //given
        Member registeredMember = Member.builder()
            .id(1L)
            .username("userA")
            .password("some")
            .email("userA@mail.com")
            .build();

        when(memberService.registerMember(any(Member.class)))
            .thenReturn(registeredMember);

        MemberRegisterDto dto = MemberRegisterDto.builder()
            .username("userA")
            .password("some")
            .email("userA@mail.com")
            .build();

        //when
        //then
        mvc.perform(post("/members/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf()))
            .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    void 회원정보_조회() throws Exception {
        //given
        String target = "userA";

        Member member = Member.builder()
            .username("userA")
            .password("pass")
            .email("userA@mail.com")
            .build();

        when(memberService.findByUsername(target))
            .thenReturn(member);

        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(target, null, null));

        //when
        //then
        mvc.perform(get("/members")
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username", "userA").exists())
            .andExpect(jsonPath("$.email", "userA@mail.com").exists());
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        MemberMapper memberMapper() {
            return new MemberMapperImpl();
        }
    }
}