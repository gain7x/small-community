package com.practice.smallcommunity.inquiry.interfaces;

import com.practice.smallcommunity.common.interfaces.CurrentUser;
import com.practice.smallcommunity.common.interfaces.dto.PageResponse;
import com.practice.smallcommunity.inquiry.InquiryChatService;
import com.practice.smallcommunity.inquiry.domain.InquiryChat;
import com.practice.smallcommunity.inquiry.domain.InquiryChatRepository;
import com.practice.smallcommunity.inquiry.interfaces.dto.InquiryChatRequest;
import com.practice.smallcommunity.inquiry.interfaces.dto.InquiryChatResponse;
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
@RequestMapping("/api/v1/members/inquiries")
public class InquiryChatController {

    private final MemberService memberService;
    private final InquiryChatService inquiryChatService;
    private final InquiryChatHandler inquiryChatHandler;
    private final InquiryChatRepository inquiryChatRepository;
    private final InquiryChatMapper mapper;

    @GetMapping
    public PageResponse<InquiryChatResponse> chats(@CurrentUser Long loginId,
                                                   @RequestParam(defaultValue = "0") int page) {
        PageRequest pageRequest = PageRequest.of(page, 10);
        Page<InquiryChat> chats = inquiryChatRepository.searchInquiryChats(loginId, pageRequest);
        return PageResponse.Ok(chats.map(mapper::toResponse));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public void chat(@CurrentUser Long loginId, @Valid @RequestBody InquiryChatRequest dto) {
        Member inquirer = memberService.findByUserId(loginId);
        InquiryChat chat = inquiryChatService.saveChat(inquirer, inquirer, dto.getContent());

        inquiryChatHandler.handleChat(chat);
    }
}
