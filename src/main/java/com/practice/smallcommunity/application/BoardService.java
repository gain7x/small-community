package com.practice.smallcommunity.application;

import com.practice.smallcommunity.domain.post.BoardRepository;
import com.practice.smallcommunity.domain.post.Post;
import com.practice.smallcommunity.domain.post.dto.BoardSearchCond;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BoardService {

    private final BoardRepository boardRepository;

    /**
     * 카테고리에 속한 게시글 목록을 검색합니다.
     * @param boardSearchCond 검색 조건
     * @param pageable 페이징 정보
     * @return 게시글 목록
     */
    public Page<Post> searchPostsInCategory(BoardSearchCond boardSearchCond, Pageable pageable) {
        return boardRepository.searchPosts(boardSearchCond, pageable);
    }
}
