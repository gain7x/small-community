package com.practice.smallcommunity.interfaces.post;

import com.practice.smallcommunity.application.BoardService;
import com.practice.smallcommunity.domain.post.Post;
import com.practice.smallcommunity.domain.post.dto.BoardSearchCond;
import com.practice.smallcommunity.interfaces.PageResponse;
import com.practice.smallcommunity.interfaces.post.dto.BoardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/categories")
public class BoardController {

    private final BoardService boardService;
    private final PostMapper mapper;

    @GetMapping("/{categoryId}/posts")
    public PageResponse<BoardResponse> findPosts(@PathVariable Long categoryId,
        @RequestParam(required = false) String title,
        @RequestParam(required = false) String text,
        Pageable pageable) {
        BoardSearchCond cond = BoardSearchCond.builder()
            .categoryId(categoryId)
            .title(title)
            .build();

        Page<Post> posts = boardService.searchPostsInCategory(cond, pageable);

        return PageResponse.Ok(posts.map(mapper::toBoard));
    }
}
