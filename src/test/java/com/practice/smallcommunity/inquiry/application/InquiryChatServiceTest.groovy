package com.practice.smallcommunity.inquiry.application

import com.practice.smallcommunity.inquiry.InquiryChatService
import com.practice.smallcommunity.inquiry.domain.InquiryChat
import com.practice.smallcommunity.inquiry.domain.InquiryChatRepository
import com.practice.smallcommunity.member.domain.Member
import com.practice.smallcommunity.testutils.DomainGenerator
import spock.lang.Specification

class InquiryChatServiceTest extends Specification {

    InquiryChatRepository inquiryChatRepository = Mock()
    InquiryChatService inquiryChatService = new InquiryChatService(inquiryChatRepository)

    Member inquirer = DomainGenerator.createMember("A")

    def "문의 채팅을 저장한다"() {
        when:
        inquiryChatService.saveChat(inquirer, inquirer, "문의 내용")

        then:
        1 * inquiryChatRepository.save({
            with(it, InquiryChat) {
                it.inquirer == inquirer
                it.sender == inquirer
                it.content == "문의 내용"
            }
        })
    }
}
