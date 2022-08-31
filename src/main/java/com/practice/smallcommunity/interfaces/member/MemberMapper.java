package com.practice.smallcommunity.interfaces.member;

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
}
