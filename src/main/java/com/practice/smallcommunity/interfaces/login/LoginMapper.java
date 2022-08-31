package com.practice.smallcommunity.interfaces.login;

import com.practice.smallcommunity.application.dto.LoginDto;
import com.practice.smallcommunity.interfaces.login.dto.LoginResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;

@Mapper(componentModel = ComponentModel.SPRING)
public interface LoginMapper {

    @Mapping(source = "member.email", target = "email")
    @Mapping(source = "member.nickname", target = "nickname")
    @Mapping(source = "member.lastPasswordChange", target = "lastPasswordChange")
    LoginResponse toResponse(LoginDto dto);
}
