package com.practice.smallcommunity.inquiry.interfaces;

import com.practice.smallcommunity.inquiry.domain.InquiryChat;
import com.practice.smallcommunity.inquiry.interfaces.dto.InquiryChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class InquiryChatHandler {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 문의 채팅을 알맞은 목적지로 전달합니다.
     */
    public void handleChat(InquiryChat chat) {
        InquiryChatMessage message = InquiryChatMessage.builder()
            .id(chat.getId())
            .inquirerId(chat.getInquirer().getId())
            .senderId(chat.getSender().getId())
            .content(chat.getContent())
            .createdDate(chat.getCreatedDate())
            .build();

        /*
         *  관리자는 전체 사용자의 문의 채팅을 수신하는 /topic/inquiry 목적지를 구독합니다.
         *  사용자는 자신의 문의 채팅만 수신 가능한 /user/topic/inquiry 목적지를 구독하며
         *      스프링 STOMP 지원이 이를 /user/{userId}/topic/inquiry 목적지로 변환하여 구독시킵니다.
         */
        messagingTemplate.convertAndSend("/topic/inquiry", message);
        messagingTemplate.convertAndSend("/user/" + chat.getInquirer().getId() + "/topic/inquiry", message);
    }
}
