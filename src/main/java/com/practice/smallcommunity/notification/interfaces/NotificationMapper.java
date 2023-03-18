package com.practice.smallcommunity.notification.interfaces;

import com.practice.smallcommunity.notification.Notification;
import com.practice.smallcommunity.notification.interfaces.dto.NotificationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;

@Mapper(componentModel = ComponentModel.SPRING)
public interface NotificationMapper {

    @Mapping(target = "postId", source = "notification.relatedPost.id")
    NotificationResponse toResponse(Notification notification);
}
