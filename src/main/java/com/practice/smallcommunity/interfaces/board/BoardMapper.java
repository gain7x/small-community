package com.practice.smallcommunity.interfaces.board;

import com.practice.smallcommunity.domain.board.Board;
import com.practice.smallcommunity.interfaces.board.dto.BoardResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

@Mapper(componentModel = ComponentModel.SPRING)
public interface BoardMapper {

    BoardResponse toResponse(Board board);
}
