package com.practice.smallcommunity.inquiry.interfaces;

import com.practice.smallcommunity.inquiry.domain.InquiryChat;
import com.practice.smallcommunity.inquiry.interfaces.dto.InquiryChatMessage;
import com.practice.smallcommunity.member.domain.MemberRole;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class InquiryChatHandler {

    private final SimpMessagingTemplate messagingTemplate;

    public void handleChat(InquiryChat chat) {
        InquiryChatMessage message = InquiryChatMessage.builder()
                .inquirerId(chat.getInquirer().getId())
                .senderId(chat.getSender().getId())
                .content(chat.getContent())
                .createdDate(chat.getCreatedDate())
                .build();

        messagingTemplate.convertAndSend("/topic/inquiry", message);

        if (chat.getSender().getMemberRole().equals(MemberRole.ADMIN)) {
            messagingTemplate.convertAndSend("/user/" + chat.getInquirer().getId() + "/topic/inquiry", message);
        }
    }
}
