package com.practice.smallcommunity.interfaces.post;

import com.practice.smallcommunity.domain.post.Post;
import com.practice.smallcommunity.interfaces.post.dto.BoardResponse;
import com.practice.smallcommunity.interfaces.post.dto.PostResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;

@Mapper(componentModel = ComponentModel.SPRING)
public interface PostMapper {

    @Mapping(target = "categoryCode", source = "category.code")
    @Mapping(target = "memberId", source = "writer.id")
    @Mapping(target = "text", source = "mainText.text")
    @Mapping(target = "solved", expression = "java(post.getAcceptedReply() != null)")
    PostResponse toResponse(Post post);

    @Mapping(target = "postId", source = "post.id")
    @Mapping(target = "memberId", source = "writer.id")
    @Mapping(target = "solved", expression = "java(post.getAcceptedReply() != null)")
    BoardResponse toBoard(Post post);
}
