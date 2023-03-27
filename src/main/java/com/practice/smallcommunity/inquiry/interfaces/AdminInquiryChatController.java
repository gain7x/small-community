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
@RequestMapping("/api/admin/members/")
public class AdminInquiryChatController {

    private final MemberService memberService;
    private final InquiryChatService inquiryChatService;
    private final InquiryChatHandler inquiryChatHandler;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{inquirerId}/inquiries")
    public void adminChat(@CurrentUser Long loginId,
                          @PathVariable("inquirerId") Long inquirerId,
                          @Valid @RequestBody InquiryChatRequest dto) {
        Member inquirer = memberService.findByUserId(inquirerId);
        Member sender = memberService.findByUserId(loginId);
        InquiryChat chat = inquiryChatService.saveChat(inquirer, sender, dto.getContent());

        inquiryChatHandler.handleChat(chat);
    }
}
