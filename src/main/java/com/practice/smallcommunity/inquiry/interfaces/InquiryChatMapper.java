package com.practice.smallcommunity.inquiry.interfaces;

import com.practice.smallcommunity.inquiry.domain.InquiryChat;
import com.practice.smallcommunity.inquiry.interfaces.dto.InquiryChatResponse;
import com.practice.smallcommunity.inquiry.interfaces.dto.LatestInquiryChatResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface InquiryChatMapper {

    @Mapping(target = "memberId", source = "inquirer.id")
    @Mapping(target = "nickname", source = "inquirer.nickname")
    @Mapping(target = "chat", source = ".")
    LatestInquiryChatResponse toLatestResponse(InquiryChat chat);

    @Mapping(target = "senderId", source = "sender.id")
    InquiryChatResponse toResponse(InquiryChat chat);
}
