package com.practice.smallcommunity.service.member;

import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.service.member.dto.MemberRegisterDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;

@Mapper(componentModel = ComponentModel.SPRING)
public interface MemberMapper {

    @Mapping(target = "id", ignore = true)
    Member toEntity(MemberRegisterDto dto);
}
