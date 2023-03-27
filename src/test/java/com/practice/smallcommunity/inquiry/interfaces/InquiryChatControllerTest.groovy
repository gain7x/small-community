package com.practice.smallcommunity.inquiry.interfaces

import com.fasterxml.jackson.databind.ObjectMapper
import com.practice.smallcommunity.inquiry.InquiryChatService
import com.practice.smallcommunity.inquiry.domain.InquiryChat
import com.practice.smallcommunity.inquiry.domain.InquiryChatRepository
import com.practice.smallcommunity.inquiry.interfaces.dto.InquiryChatRequest
import com.practice.smallcommunity.member.application.MemberService
import com.practice.smallcommunity.member.domain.Member
import com.practice.smallcommunity.testutils.DomainGenerator
import com.practice.smallcommunity.testutils.interfaces.RestTest
import com.practice.smallcommunity.testutils.interfaces.WithMockMember
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import spock.lang.Specification

import static com.practice.smallcommunity.testutils.interfaces.RestDocsHelper.*
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields
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

    @Autowired
    private ObjectMapper objectMapper

    @Autowired
    MockMvc mvc

    @WithMockMember
    def "회원의 문의 채팅"() throws Exception {
        given:
        Long inquirerId = 1L
        Member inquirer = Spy(DomainGenerator.createMember("A"))
        inquirer.id >> inquirerId
        memberService.findByUserId(inquirerId) >> inquirer
        inquiryChatService.saveChat(inquirer, inquirer, "문의 내용") >>
                DomainGenerator.createInquiryChat(inquirer, inquirer, "문의 내용")

        when:
        InquiryChatRequest req = new InquiryChatRequest("문의 내용")
        ResultActions result = mvc.perform(post("/api/v1/members/inquiries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
                .accept(MediaType.APPLICATION_JSON))

        then:
        ConstrainedFields fields = getConstrainedFields(InquiryChatRequest.class)
        result.andExpect(status().isCreated())
                .andDo(generateDocument("inquiry",
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
}
