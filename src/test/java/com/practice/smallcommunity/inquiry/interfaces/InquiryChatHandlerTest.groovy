package com.practice.smallcommunity.inquiry.interfaces

import com.fasterxml.jackson.databind.ObjectMapper
import com.practice.smallcommunity.inquiry.domain.InquiryChat
import com.practice.smallcommunity.inquiry.interfaces.dto.InquiryChatMessage
import com.practice.smallcommunity.member.domain.Member
import com.practice.smallcommunity.member.domain.MemberRole
import com.practice.smallcommunity.testutils.DomainGenerator
import com.practice.smallcommunity.testutils.TestSecurityUtil
import com.practice.smallcommunity.testutils.interfaces.TestChannelInterceptor
import com.practice.smallcommunity.testutils.interfaces.WebSocketTest
import com.practice.smallcommunity.testutils.interfaces.WebSocketUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.messaging.Message
import org.springframework.messaging.MessageDeliveryException
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.AbstractSubscribableChannel
import org.springframework.security.access.AccessDeniedException
import spock.lang.Specification

import java.time.LocalDateTime

@WebSocketTest
@WebMvcTest(InquiryChatHandler.class)
class InquiryChatHandlerTest extends Specification {

    @Autowired
    InquiryChatHandler inquiryChatHandler

    @Autowired
    ObjectMapper mapper

    @Autowired
    private AbstractSubscribableChannel clientInboundChannel

    @Autowired
    private AbstractSubscribableChannel clientOutboundChannel

    @Autowired
    private AbstractSubscribableChannel brokerChannel

    private TestChannelInterceptor clientOutboundChannelInterceptor

    private TestChannelInterceptor brokerChannelInterceptor

    def setup() {
        clientOutboundChannelInterceptor = new TestChannelInterceptor()
        brokerChannelInterceptor = new TestChannelInterceptor()

        clientOutboundChannel.addInterceptor(clientOutboundChannelInterceptor)
        brokerChannel.addInterceptor(brokerChannelInterceptor)
    }

    def "회원이 회원용 문의 토픽을 구독한다"() {
        given:
        Message<byte[]> message = WebSocketUtils.createSubscribeMessage(
                "/user/topic/inquiry", TestSecurityUtil.createAuthentication("USER"), new byte[0])

        expect:
        clientInboundChannel.send(message)
    }

    def "회원이 관리자용 전체 문의 토픽을 구독하려하면 예외가 발생한다"() {
        given:
        Message<byte[]> message = WebSocketUtils.createSubscribeMessage(
                "/topic/inquiry", TestSecurityUtil.createAuthentication("USER"), new byte[0])

        when:
        clientInboundChannel.send(message)

        then:
        def e = thrown(MessageDeliveryException)
        e.cause instanceof AccessDeniedException
    }

    def "모든 문의 채팅은 관리자용 전체 문의 토픽에 브로드캐스트된다"() throws InterruptedException {
        given:
        Member inquirer = Spy(DomainGenerator.createMember("A"))
        InquiryChat chat = Spy(DomainGenerator.createInquiryChat(inquirer, inquirer, "문의 내용"))
        inquirer.id >> 1L
        chat.createdDate >> LocalDateTime.now()

        brokerChannelInterceptor.setIncludedDestinations("/topic/inquiry")

        when:
        inquiryChatHandler.handleChat(chat)

        then:
        Message<?> sent = brokerChannelInterceptor.awaitMessage(2)
        InquiryChatMessage message = mapper.readValue((byte[]) sent.payload, InquiryChatMessage.class)
        with(message) {
            inquirerId == inquirer.id
            senderId == inquirer.id
            content == chat.content
            createdAt == chat.createdDate
        }
    }

    def "관리자의 문의 채팅은 회원별 문의 토픽에 브로드캐스트된다" () {
        given:
        Member inquirer = Spy(DomainGenerator.createMember("A"))
        Member sender = Spy(DomainGenerator.createMember("B"))
        InquiryChat chat = Spy(DomainGenerator.createInquiryChat(inquirer, sender, "문의 내용"))
        inquirer.id >> 1L
        sender.id >> 2L
        sender.memberRole >> MemberRole.ADMIN
        chat.createdDate >> LocalDateTime.now()

        brokerChannelInterceptor.setIncludedDestinations("/user/**")

        when:
        inquiryChatHandler.handleChat(chat)

        then:
        Message<?> sent = brokerChannelInterceptor.awaitMessage(2)
        StompHeaderAccessor messageHeaders = StompHeaderAccessor.wrap(sent)
        InquiryChatMessage message = mapper.readValue((byte[]) sent.payload, InquiryChatMessage.class)

        messageHeaders.destination == "/user/1/topic/inquiry"
        with(message) {
            inquirerId == inquirer.id
            senderId == sender.id
            content == chat.content
            createdAt == chat.createdDate
        }
    }

    def "관리자가 전체회원의 문의 토픽을 구독한다"() {
        given:
        Message<byte[]> message = WebSocketUtils.createSubscribeMessage(
                "/topic/inquiry", TestSecurityUtil.createAuthentication("ADMIN"), new byte[0])

        expect:
        clientInboundChannel.send(message)
    }
}
