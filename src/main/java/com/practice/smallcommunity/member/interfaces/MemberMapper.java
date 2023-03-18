package com.practice.smallcommunity.member.interfaces;

import com.practice.smallcommunity.post.domain.Post;
import com.practice.smallcommunity.reply.Reply;
import com.practice.smallcommunity.member.interfaces.dto.MemberContentResponse;
import com.practice.smallcommunity.member.interfaces.dto.MemberResponse;
import com.practice.smallcommunity.member.interfaces.dto.MemberRegisterRequest;
import com.practice.smallcommunity.member.domain.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;

@Mapper(componentModel = ComponentModel.SPRING)
public interface MemberMapper {

    @Mapping(target = "memberRole", ignore = true)
    Member toEntity(MemberRegisterRequest dto);

    MemberResponse toDto(Member member);

    @Mapping(target = "postId", source = "post.id")
    MemberContentResponse toContentResponse(Post post);

    @Mapping(target = "postId", source = "post.id")
    @Mapping(target = "title", source = "reply.post.title")
    MemberContentResponse toContentResponse(Reply reply);
}
