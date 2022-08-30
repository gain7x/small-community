package com.practice.smallcommunity.interfaces.post;

import com.practice.smallcommunity.domain.post.Post;
import com.practice.smallcommunity.interfaces.post.dto.PostResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;

@Mapper(componentModel = ComponentModel.SPRING)
public interface PostMapper {

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "memberId", source = "writer.id")
    @Mapping(target = "text", source = "content.text")
    PostResponse toResponse(Post post);
}
