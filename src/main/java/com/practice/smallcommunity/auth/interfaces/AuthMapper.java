package com.practice.smallcommunity.auth.interfaces;

import com.practice.smallcommunity.auth.application.dto.AuthDto;
import com.practice.smallcommunity.auth.interfaces.dto.LoginResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;

@Mapper(componentModel = ComponentModel.SPRING)
public interface AuthMapper {

    @Mapping(target = "admin", expression = "java(dto.getMember().getMemberRole().equals(com.practice.smallcommunity.member.MemberRole.ADMIN) ? true : null)")
    @Mapping(target = "accessTokenExpires", expression = "java(dto.getAccessTokenExpires().getTime())")
    @Mapping(target = "refreshTokenExpires", expression = "java(dto.getRefreshTokenExpires().getTime())")
    @Mapping(source = "member.id", target = "memberId")
    @Mapping(source = "member.email", target = "email")
    @Mapping(source = "member.nickname", target = "nickname")
    LoginResponse toResponse(AuthDto dto);
}
