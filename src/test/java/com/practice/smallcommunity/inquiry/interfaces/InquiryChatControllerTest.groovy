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
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import spock.lang.Specification

import java.time.LocalDateTime

import static com.practice.smallcommunity.testutils.DomainGenerator.*
import static com.practice.smallcommunity.testutils.interfaces.RestDocsHelper.*
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import static org.springframework.restdocs.request.RequestDocumentation.*
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@RestTest
@WebMvcTest(InquiryChatController.class)
class InquiryChatControllerTest extends Specification {

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

    @WithMockMember
    def "문의 채팅 내역 조회"() throws Exception {
        given:
        Member inquirer = Spy(createMember("A"))
        inquirer.id >> 1L
        InquiryChat chat = Spy(createInquiryChat(inquirer, inquirer, "문의 내용 1"))
        chat.id >> 1L
        chat.createdDate >> LocalDateTime.now()
        inquiryChatRepository.findChatsByInquirerId(1L, _ as Pageable) >> new PageImpl<>(List.of(chat))

        when:
        ResultActions result = mvc.perform(get("/api/v1/members/inquiries")
                .param("page", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))

        then:
        result.andExpect(status().isOk())
                .andDo(generateDocument("inquiry", "문의 채팅 내역 조회",
                        requestParameters(
                                parameterWithName("page").description("페이지 번호(0번이 가장 최근 채팅)")
                        ),
                        responseFields(
                                pageData(),
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("문의 채팅 ID"),
                                fieldWithPath("senderId").type(JsonFieldType.NUMBER).description("채팅 전송자 ID"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("채팅 내용"),
                                fieldWithPath("createdDate").type(JsonFieldType.STRING).description("채팅 생성 시간(내림차순 정렬)")
                        )))
    }

    @WithMockMember
    def "문의 채팅 저장"() throws Exception {
        given:
        Long inquirerId = 1L
        Member inquirer = Spy(createMember("A"))
        inquirer.id >> inquirerId
        memberService.findByUserId(inquirerId) >> inquirer
        inquiryChatService.saveChat(inquirer, inquirer, "문의 내용") >>
                createInquiryChat(inquirer, inquirer, "문의 내용")

        when:
        InquiryChatRequest req = new InquiryChatRequest("문의 내용")
        ResultActions result = mvc.perform(post("/api/v1/members/inquiries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
                .accept(MediaType.APPLICATION_JSON))

        then:
        ConstrainedFields fields = getConstrainedFields(InquiryChatRequest.class)
        result.andExpect(status().isCreated())
                .andDo(generateDocument("inquiry", "문의 채팅 저장",
                        requestFields(
                                fields.withPath("content").type(JsonFieldType.STRING).description("문의 내용")
                        )))

        1 * inquiryChatHandler.handleChat({
            with(it, InquiryChat) {
                it.inquirer.id == inquirerId
                it.sender.id == inquirerId
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
