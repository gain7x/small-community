package com.practice.smallcommunity.inquiry.domain


import com.practice.smallcommunity.member.domain.Member
import com.practice.smallcommunity.member.domain.MemberRepository
import com.practice.smallcommunity.testutils.DomainGenerator
import org.hibernate.Hibernate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.PageRequest
import spock.lang.Specification

import javax.persistence.EntityManager

@DataJpaTest
class InquiryChatRepositoryTest extends Specification {

    @Autowired
    InquiryChatRepository inquiryChatRepository

    @Autowired
    MemberRepository memberRepository

    @Autowired
    EntityManager em

    Member inquirer = DomainGenerator.createMember("A")

    def setup() {
        memberRepository.save(inquirer)
    }

    def "문의 채팅을 저장한다"() {
        given:
        def item = DomainGenerator.createInquiryChat(inquirer, inquirer, "문의 내용")

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
        def item = DomainGenerator.createInquiryChat(inquirer, inquirer, "문의 내용")

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
        def item = DomainGenerator.createInquiryChat(inquirer, inquirer, "문의 내용")

        when:
        def savedItem = inquiryChatRepository.save(item)
        inquiryChatRepository.delete(savedItem)

        then:
        inquiryChatRepository.findById(savedItem.id).isEmpty()
    }

    def "각 사용자의 최근 채팅을 조회한다"() {
        given:
        def inquirer2 = DomainGenerator.createMember("B")
        memberRepository.save(inquirer2)
        def latestItem1
        def latestItem2
        for (i in 0..<3) {
            latestItem1 = DomainGenerator.createInquiryChat(inquirer, inquirer, "문의 내용")
            latestItem2 = DomainGenerator.createInquiryChat(inquirer2, inquirer2, "문의 내용")
            inquiryChatRepository.save(latestItem1)
            inquiryChatRepository.save(latestItem2)
        }
        em.flush()
        em.clear()

        when:
        def pageRequest = PageRequest.of(0, 5)
        def chats = inquiryChatRepository.findEachInquirerLatestChatFetchJoin(pageRequest)

        then:
        chats.content.size() == 2
        chats.content.inquirer.id.containsAll([inquirer.id, inquirer2.id])
        chats.content.id.containsAll([latestItem1.id, latestItem2.id])
        chats.content.forEach(chat -> Hibernate.isInitialized(chat.inquirer))
    }
}
