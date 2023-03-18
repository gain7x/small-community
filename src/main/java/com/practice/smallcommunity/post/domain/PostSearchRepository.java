package com.practice.smallcommunity.post.domain;

import com.practice.smallcommunity.post.domain.dto.BoardSearchCond;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostSearchRepository {

    /**
     * 카테고리에 속한 게시글 목록을 반환합니다.
     * @param cond 검색 조건
     * @param pageable 페이징 정보
     * @return 게시글 목록
     */
    Page<Post> searchPosts(BoardSearchCond cond, Pageable pageable);
}