package com.practice.smallcommunity.inquiry.interfaces

import com.fasterxml.jackson.databind.ObjectMapper
import com.practice.smallcommunity.inquiry.InquiryChatService
import com.practice.smallcommunity.inquiry.domain.InquiryChat
import com.practice.smallcommunity.inquiry.interfaces.dto.InquiryChatRequest
import com.practice.smallcommunity.member.application.MemberService
import com.practice.smallcommunity.member.domain.Member
import com.practice.smallcommunity.testutils.DomainGenerator
import com.practice.smallcommunity.testutils.interfaces.RestDocsHelper
import com.practice.smallcommunity.testutils.interfaces.RestTest
import com.practice.smallcommunity.testutils.interfaces.WithMockMember
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import spock.lang.Specification

import static com.practice.smallcommunity.testutils.interfaces.RestDocsHelper.generateDocument
import static com.practice.smallcommunity.testutils.interfaces.RestDocsHelper.getConstrainedFields
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@RestTest
@WebMvcTest(AdminInquiryChatController.class)
class AdminInquiryChatControllerTest extends Specification {

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

    @WithMockMember(roles = "ADMIN")
    def "관리자의 문의 채팅"() throws Exception {
        given:
        Long senderId = 1L
        Long inquirerId = 2L
        String content = "문의 내용"

        Member sender = Spy(DomainGenerator.createMember("A"))
        sender.id >> senderId
        memberService.findByUserId(senderId) >> sender

        Member inquirer = Spy(DomainGenerator.createMember("B"))
        inquirer.id >> inquirerId
        memberService.findByUserId(inquirerId) >> inquirer

        inquiryChatService.saveChat(inquirer, sender, content) >>
                DomainGenerator.createInquiryChat(inquirer, sender, content)

        when:
        InquiryChatRequest req = new InquiryChatRequest(content)
        ResultActions result = mvc.perform(
                RestDocumentationRequestBuilders.post("/api/admin/members/{inquirerId}/inquiry", inquirerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .accept(MediaType.APPLICATION_JSON))

        then:
        RestDocsHelper.ConstrainedFields fields = getConstrainedFields(InquiryChatRequest.class)
        result.andExpect(status().isCreated())
                .andDo(generateDocument("admin-inquiry",
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
}
