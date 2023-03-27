package com.practice.smallcommunity.inquiry.domain


import com.practice.smallcommunity.member.domain.Member
import com.practice.smallcommunity.member.domain.MemberRepository
import com.practice.smallcommunity.testutils.DomainGenerator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import spock.lang.Specification

@DataJpaTest
class InquiryChatRepositoryTest extends Specification {

    @Autowired
    InquiryChatRepository inquiryChatRepository

    @Autowired
    MemberRepository memberRepository

    Member inquirer = DomainGenerator.createMember("A")

    def setup() {
        memberRepository.save(inquirer)
    }

    def "문의 채팅을 저장한다"() {
        given:
        def item = InquiryChat.builder()
                .inquirer(inquirer)
                .sender(inquirer)
                .content("문의 내용")
                .build()

        when:
        def savedItem = inquiryChatRepository.save(item)

        then:
        item.id != null
        item.id == savedItem.id
        with(savedItem) {
            inquirer.id == item.inquirer.id
            sender.id == item.sender.id
        }
    }

    def "문의 채팅을 조회한다"() {
        given:
        def item = InquiryChat.builder()
                .inquirer(inquirer)
                .sender(inquirer)
                .content("문의 내용")
                .build()

        when:
        def savedItem = inquiryChatRepository.save(item)
        def findItem = inquiryChatRepository.findById(savedItem.id).get()

        then:
        with(findItem) {
            id == savedItem.id
            inquirer.id == savedItem.inquirer.id
            sender.id == savedItem.sender.id
            content == savedItem.content
        }
    }

    def "문의 채팅을 삭제한다"() {
        given:
        def item = InquiryChat.builder()
                .inquirer(inquirer)
                .sender(inquirer)
                .content("문의 내용")
                .build()

        when:
        def savedItem = inquiryChatRepository.save(item)
        inquiryChatRepository.delete(savedItem)

        then:
        inquiryChatRepository.findById(savedItem.id).isEmpty()
    }
}
