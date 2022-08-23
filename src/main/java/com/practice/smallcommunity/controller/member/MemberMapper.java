package com.practice.smallcommunity.controller.member;

import com.practice.smallcommunity.controller.member.dto.MemberDto;
import com.practice.smallcommunity.controller.member.dto.MemberRegisterRequest;
import com.practice.smallcommunity.domain.member.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;

@Mapper(componentModel = ComponentModel.SPRING)
public interface MemberMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "memberRole", ignore = true)
    Member toEntity(MemberRegisterRequest dto);

    MemberDto toDto(Member member);
}
