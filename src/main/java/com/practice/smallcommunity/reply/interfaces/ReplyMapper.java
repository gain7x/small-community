package com.practice.smallcommunity.reply.interfaces;

import com.practice.smallcommunity.reply.Reply;
import com.practice.smallcommunity.reply.interfaces.dto.ReplyResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;

@Mapper(componentModel = ComponentModel.SPRING)
public interface ReplyMapper {

    @Mapping(source = "id", target = "replyId")
    @Mapping(source = "writer.id", target = "memberId")
    ReplyResponse toResponse(Reply reply);
}
