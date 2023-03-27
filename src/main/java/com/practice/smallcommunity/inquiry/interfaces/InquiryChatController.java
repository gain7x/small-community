package com.practice.smallcommunity.inquiry.interfaces;

import com.practice.smallcommunity.common.interfaces.CurrentUser;
import com.practice.smallcommunity.inquiry.InquiryChatService;
import com.practice.smallcommunity.inquiry.domain.InquiryChat;
import com.practice.smallcommunity.inquiry.interfaces.dto.InquiryChatRequest;
import com.practice.smallcommunity.member.application.MemberService;
import com.practice.smallcommunity.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/members/inquiry")
public class InquiryChatController {

    private final MemberService memberService;
    private final InquiryChatService inquiryChatService;
    private final InquiryChatHandler inquiryChatHandler;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public void chat(@CurrentUser Long loginId, @Valid @RequestBody InquiryChatRequest dto) {
        Member inquirer = memberService.findByUserId(loginId);
        InquiryChat chat = inquiryChatService.saveChat(inquirer, inquirer, dto.getContent());

        inquiryChatHandler.handleChat(chat);
    }
}
