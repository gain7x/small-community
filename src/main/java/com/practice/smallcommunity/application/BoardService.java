package com.practice.smallcommunity.application;

import com.practice.smallcommunity.application.exception.ValidationError;
import com.practice.smallcommunity.application.exception.ValidationErrorException;
import com.practice.smallcommunity.application.exception.ValidationErrorStatus;
import com.practice.smallcommunity.domain.board.Board;
import com.practice.smallcommunity.domain.board.BoardRepository;
import com.practice.smallcommunity.domain.category.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class BoardService {

    private final CategoryService categoryService;
    private final BoardRepository boardRepository;

    /**
     * 게시판을 등록하고, 성공하면 등록된 게시판 정보를 반환합니다.
     * @param categoryId 카테고리 ID
     * @param name 게시판 이름
     * @param enable 활성 여부
     * @return 등록된 게시판 정보
     * @throws ValidationErrorException
     *          카테고리를 찾을 수 없는 경우
     */
    @Transactional
    public Board register(Long categoryId, String name, boolean enable) {
        Category category = categoryService.findOne(categoryId);
        Board board = Board.builder()
            .category(category)
            .name(name)
            .enable(enable)
            .build();

        return boardRepository.save(board);
    }

    /**
     * 게시판 번호로 게시판을 조회합니다.
     * @param boardId 게시판 번호
     * @return 게시판
     * @throws ValidationErrorException
     *          번호에 해당하는 게시판이 없는 경우
     */
    public Board findOne(Long boardId) {
        return boardRepository.findById(boardId)
            .orElseThrow(() -> new ValidationErrorException("게시판을 찾을 수 없습니다.",
                ValidationError.of(ValidationErrorStatus.NOT_FOUND, "boardId")
            ));
    }
}
