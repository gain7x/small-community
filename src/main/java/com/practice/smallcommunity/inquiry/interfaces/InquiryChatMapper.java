package com.practice.smallcommunity.inquiry.interfaces;

import com.practice.smallcommunity.inquiry.domain.InquiryChat;
import com.practice.smallcommunity.inquiry.interfaces.dto.InquiryChatResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface InquiryChatMapper {

    @Mapping(target = "senderId", source = "sender.id")
    InquiryChatResponse toResponse(InquiryChat chat);
}
