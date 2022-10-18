package com.practice.smallcommunity.interfaces.auth;

import com.practice.smallcommunity.application.auth.dto.AuthDto;
import com.practice.smallcommunity.interfaces.auth.dto.LoginResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;

@Mapper(componentModel = ComponentModel.SPRING)
public interface AuthMapper {

    @Mapping(target = "admin", expression = "java(dto.getMember().getMemberRole().equals(com.practice.smallcommunity.domain.member.MemberRole.ADMIN) ? true : null)")
    @Mapping(target = "accessTokenExpires", expression = "java(dto.getAccessTokenExpires().getTime())")
    @Mapping(target = "refreshTokenExpires", expression = "java(dto.getRefreshTokenExpires().getTime())")
    @Mapping(source = "member.id", target = "memberId")
    @Mapping(source = "member.email", target = "email")
    @Mapping(source = "member.nickname", target = "nickname")
    @Mapping(source = "member.lastPasswordChange", target = "lastPasswordChange")
    LoginResponse toResponse(AuthDto dto);
}
