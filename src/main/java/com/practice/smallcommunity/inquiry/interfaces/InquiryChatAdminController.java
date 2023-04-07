package com.practice.smallcommunity.inquiry.interfaces;

import com.practice.smallcommunity.common.interfaces.CurrentUser;
import com.practice.smallcommunity.common.interfaces.dto.PageResponse;
import com.practice.smallcommunity.inquiry.InquiryChatService;
import com.practice.smallcommunity.inquiry.domain.InquiryChat;
import com.practice.smallcommunity.inquiry.domain.InquiryChatRepository;
import com.practice.smallcommunity.inquiry.interfaces.dto.InquiryChatRequest;
import com.practice.smallcommunity.inquiry.interfaces.dto.InquiryChatResponse;
import com.practice.smallcommunity.inquiry.interfaces.dto.LatestInquiryChatResponse;
import com.practice.smallcommunity.member.application.MemberService;
import com.practice.smallcommunity.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/members/")
public class InquiryChatAdminController {

    private final MemberService memberService;
    private final InquiryChatService inquiryChatService;
    private final InquiryChatHandler inquiryChatHandler;
    private final InquiryChatRepository inquiryChatRepository;
    private final InquiryChatMapper mapper;

    @GetMapping("/inquiries")
    public PageResponse<LatestInquiryChatResponse> eachInquirerLatestChats(@RequestParam(defaultValue = "0") int page) {
        PageRequest pageRequest = PageRequest.of(page, 10);
        Page<InquiryChat> chats = inquiryChatRepository.findEachInquirerLatestChatFetchJoin(pageRequest);
        return PageResponse.Ok(chats.map(mapper::toLatestResponse));
    }

    @GetMapping("/{inquirerId}/inquiries")
    public PageResponse<InquiryChatResponse> chats(@PathVariable("inquirerId") Long inquirerId,
                                                   @RequestParam(defaultValue = "0") int page) {
        PageRequest pageRequest = PageRequest.of(page, 10);
        Page<InquiryChat> chats = inquiryChatRepository.findChatsByInquirerId(inquirerId, pageRequest);
        return PageResponse.Ok(chats.map(mapper::toResponse));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{inquirerId}/inquiries")
    public void chat(@CurrentUser Long loginId,
                     @PathVariable("inquirerId") Long inquirerId,
                     @Valid @RequestBody InquiryChatRequest dto) {
        Member inquirer = memberService.findByUserId(inquirerId);
        Member sender = memberService.findByUserId(loginId);
        InquiryChat chat = inquiryChatService.saveChat(inquirer, sender, dto.getContent());

        inquiryChatHandler.handleChat(chat);
    }
}
