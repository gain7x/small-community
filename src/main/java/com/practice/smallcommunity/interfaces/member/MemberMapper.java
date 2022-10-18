package com.practice.smallcommunity.interfaces.member;

import com.practice.smallcommunity.domain.post.Post;
import com.practice.smallcommunity.domain.reply.Reply;
import com.practice.smallcommunity.interfaces.member.dto.MemberContentResponse;
import com.practice.smallcommunity.interfaces.member.dto.MemberResponse;
import com.practice.smallcommunity.interfaces.member.dto.MemberRegisterRequest;
import com.practice.smallcommunity.domain.member.Member;
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
