package com.practice.smallcommunity.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import com.practice.smallcommunity.application.exception.ValidationErrorException;
import com.practice.smallcommunity.domain.board.Board;
import com.practice.smallcommunity.domain.board.BoardRepository;
import com.practice.smallcommunity.domain.category.Category;
import com.practice.smallcommunity.utils.DomainGenerator;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {

    @Mock
    BoardRepository boardRepository;

    BoardService boardService;

    Category category = DomainGenerator.createCategory("개발");

    @BeforeEach
    void setUp() {
        boardService = new BoardService(boardRepository);
    }

    @Test
    void 게시판을_등록한다() {
        //given
        when(boardRepository.save(any(Board.class)))
            .thenAnswer(AdditionalAnswers.returnsFirstArg());

        //when
        Board registeredBoard = boardService.register(category, "Java", true);

        //then
        assertThat(registeredBoard).isNotNull();
    }

    @Test
    void 번호로_게시판을_검색한다() {
        //given
        Long targetId = 1L;
        Board board = DomainGenerator.createBoard(category, "Java");

        when(boardRepository.findById(targetId))
            .thenReturn(Optional.of(board));

        //when
        Board findBoard = boardService.findOne(targetId);

        //then
        assertThat(findBoard).isNotNull();
    }

    @Test
    void 번호검색_시_동일한_번호의_게시판이_없으면_예외를_던진다() {
        //given
        Long targetId = 1L;

        when(boardRepository.findById(targetId))
            .thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> boardService.findOne(targetId))
            .isInstanceOf(ValidationErrorException.class);
    }
}