package com.practice.smallcommunity.inquiry;

import com.practice.smallcommunity.inquiry.domain.InquiryChat;
import com.practice.smallcommunity.inquiry.domain.InquiryChatRepository;
import com.practice.smallcommunity.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class InquiryChatService {

    private final InquiryChatRepository inquiryChatRepository;

    public InquiryChat saveChat(Member inquirer, Member sender, String content) {
        InquiryChat inquiryChat = InquiryChat.builder()
                .inquirer(inquirer)
                .sender(sender)
                .content(content)
                .build();

        return inquiryChatRepository.save(inquiryChat);
    }
}