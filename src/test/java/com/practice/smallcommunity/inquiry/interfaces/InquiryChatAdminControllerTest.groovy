package com.practice.smallcommunity.inquiry.interfaces

import com.fasterxml.jackson.databind.ObjectMapper
import com.practice.smallcommunity.inquiry.InquiryChatService
import com.practice.smallcommunity.inquiry.domain.InquiryChat
import com.practice.smallcommunity.inquiry.domain.InquiryChatRepository
import com.practice.smallcommunity.inquiry.interfaces.dto.InquiryChatRequest
import com.practice.smallcommunity.member.application.MemberService
import com.practice.smallcommunity.member.domain.Member
import com.practice.smallcommunity.testutils.interfaces.RestTest
import com.practice.smallcommunity.testutils.interfaces.WithMockMember
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import spock.lang.Specification

import java.time.LocalDateTime

import static com.practice.smallcommunity.testutils.DomainGenerator.createInquiryChat
import static com.practice.smallcommunity.testutils.DomainGenerator.createMember
import static com.practice.smallcommunity.testutils.interfaces.RestDocsHelper.*
import static org.springframework.restdocs.payload.PayloadDocumentation.*
import static org.springframework.restdocs.request.RequestDocumentation.*
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@RestTest
@WebMvcTest(InquiryChatAdminController.class)
class InquiryChatAdminControllerTest extends Specification {

    @SpringBean
    private MemberService memberService = Mock()

    @SpringBean
    private InquiryChatService inquiryChatService = Mock()

    @SpringBean
    private InquiryChatHandler inquiryChatHandler = Mock()

    @SpringBean
    private InquiryChatRepository inquiryChatRepository = Mock()

    @Autowired
    private ObjectMapper objectMapper

    @Autowired
    MockMvc mvc

    @WithMockMember(roles = "ADMIN")
    def "각 회원별 최근 문의 채팅 조회"() throws Exception {
        given:
        Long inquirerId = 1L
        Member inquirer = Spy(createMember("A"))
        inquirer.id >> inquirerId
        InquiryChat chat = Spy(createInquiryChat(inquirer, inquirer, "문의 내용 1"))
        chat.id >> 10L
        chat.createdDate >> LocalDateTime.now()
        inquiryChatRepository.findEachInquirerLatestChatFetchJoin(_ as Pageable) >> new PageImpl<>(List.of(chat))

        when:
        ResultActions result = mvc.perform(get("/api/admin/members/inquiries")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))

        then:
        result.andExpect(status().isOk())
        .andDo(generateDocument("admin/inquiry", "각 회원별 최근 문의 채팅 조회",
                responseFields(
                        pageData(),
                        fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("문의자 ID"),
                        fieldWithPath("nickname").type(JsonFieldType.STRING).description("문의자 닉네임"),
                        fieldWithPath("chat.id").type(JsonFieldType.NUMBER).description("문의 채팅 ID"),
                        fieldWithPath("chat.senderId").type(JsonFieldType.NUMBER).description("채팅 전송자 ID"),
                        fieldWithPath("chat.content").type(JsonFieldType.STRING).description("채팅 내용"),
                        fieldWithPath("chat.createdDate").type(JsonFieldType.STRING).description("채팅 생성 시간(내림차순 정렬)")
                )
        ))
    }

    @WithMockMember(roles = "ADMIN")
    def "회원의 문의 채팅 내역 조회"() throws Exception {
        given:
        Long senderId = 1L
        Long inquirerId = 2L
        Member sender = Spy(createMember("A"))
        sender.id >> senderId
        Member inquirer = Spy(createMember("B"))
        inquirer.id >> inquirerId
        InquiryChat chat = Spy(createInquiryChat(inquirer, sender, "문의 내용 1"))
        chat.id >> 1L
        chat.createdDate >> LocalDateTime.now()
        inquiryChatRepository.findChatsByInquirerId(inquirerId, _ as Pageable) >> new PageImpl<>(List.of(chat))

        when:
        ResultActions result = mvc.perform(
                RestDocumentationRequestBuilders.get("/api/admin/members/{inquirerId}/inquiries", inquirerId)
                        .param("page", "0")
                        .accept(MediaType.APPLICATION_JSON))

        then:
        result.andExpect(status().isOk())
        .andDo(generateDocument("admin/inquiry", "회원의 문의 채팅 내역 조회",
                pathParameters(
                        parameterWithName("inquirerId").description("문의 회원 ID")
                ),
                requestParameters(
                        parameterWithName("page").description("페이지 번호(0번이 가장 최근 채팅)")
                ),
                responseFields(
                        pageData(),
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("문의 채팅 ID"),
                        fieldWithPath("senderId").type(JsonFieldType.NUMBER).description("채팅 전송자 ID"),
                        fieldWithPath("content").type(JsonFieldType.STRING).description("채팅 내용"),
                        fieldWithPath("createdDate").type(JsonFieldType.STRING).description("채팅 생성 시간(내림차순 정렬)")
                )
        ))
    }

    @WithMockMember(roles = "ADMIN")
    def "문의 채팅 저장"() throws Exception {
        given:
        Long senderId = 1L
        Long inquirerId = 2L
        String content = "문의 내용"

        Member sender = Spy(createMember("A"))
        sender.id >> senderId
        memberService.findByUserId(senderId) >> sender

        Member inquirer = Spy(createMember("B"))
        inquirer.id >> inquirerId
        memberService.findByUserId(inquirerId) >> inquirer

        inquiryChatService.saveChat(inquirer, sender, content) >>
                createInquiryChat(inquirer, sender, content)

        when:
        InquiryChatRequest req = new InquiryChatRequest(content)
        ResultActions result = mvc.perform(
                RestDocumentationRequestBuilders.post("/api/admin/members/{inquirerId}/inquiries", inquirerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .accept(MediaType.APPLICATION_JSON))

        then:
        ConstrainedFields fields = getConstrainedFields(InquiryChatRequest.class)
        result.andExpect(status().isCreated())
                .andDo(generateDocument("admin/inquiry", "문의 채팅 저장",
                        pathParameters(
                                parameterWithName("inquirerId").description("문의 회원 ID")
                        ),
                        requestFields(
                                fields.withPath("content").type(JsonFieldType.STRING).description("문의 내용")
                        )))

        1 * inquiryChatHandler.handleChat({
            with(it, InquiryChat) {
                it.inquirer.id == inquirerId
                it.sender.id == senderId
                it.content == content
            }
        })
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        InquiryChatMapper inquiryChatMapper() {
            return new InquiryChatMapperImpl()
        }
    }
}
