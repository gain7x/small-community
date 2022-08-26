package com.practice.smallcommunity.interfaces.board;

import com.practice.smallcommunity.application.BoardService;
import com.practice.smallcommunity.application.CategoryService;
import com.practice.smallcommunity.application.dto.BoardDto;
import com.practice.smallcommunity.domain.board.Board;
import com.practice.smallcommunity.domain.category.Category;
import com.practice.smallcommunity.interfaces.board.dto.BoardRequest;
import com.practice.smallcommunity.interfaces.board.dto.BoardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/boards")
public class BoardController {

    private final CategoryService categoryService;
    private final BoardService boardService;
    private final BoardMapper mapper;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public void register(@RequestBody BoardRequest dto) {
        Category category = categoryService.findOne(dto.getCategoryId());
        BoardDto boardDto = BoardDto.builder()
            .category(category)
            .name(dto.getName())
            .enable(dto.isEnable())
            .build();
        boardService.register(boardDto);
    }

    @GetMapping("/{boardId}")
    public BoardResponse find(@PathVariable Long boardId) {
        Board findBoard = boardService.findOne(boardId);
        return mapper.toResponse(findBoard);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/{boardId}")
    public void update(@PathVariable Long boardId, @RequestBody BoardRequest dto) {
        Category category = categoryService.findOne(dto.getCategoryId());
        BoardDto boardDto = BoardDto.builder()
            .category(category)
            .name(dto.getName())
            .enable(dto.isEnable())
            .build();

        boardService.update(boardId, boardDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{boardId}")
    public void delete(@PathVariable Long boardId) {
        boardService.delete(boardId);
    }
}
