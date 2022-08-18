package com.practice.smallcommunity.controller.mapper;

import com.practice.smallcommunity.controller.dto.MemberRegisterDto;
import com.practice.smallcommunity.domain.member.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;

@Mapper(componentModel = ComponentModel.SPRING)
public interface MemberMapper {

    MemberRegisterDto toRegisterDto(Member entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lastPasswordChange", ignore = true)
    Member toEntity(MemberRegisterDto dto);
}
