package com.practice.smallcommunity.interfaces.reply;

import com.practice.smallcommunity.domain.reply.Reply;
import com.practice.smallcommunity.interfaces.reply.dto.ReplyResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;

@Mapper(componentModel = ComponentModel.SPRING)
public interface ReplyMapper {

    @Mapping(source = "id", target = "replyId")
    @Mapping(source = "writer.id", target = "memberId")
    ReplyResponse toResponse(Reply reply);
}
