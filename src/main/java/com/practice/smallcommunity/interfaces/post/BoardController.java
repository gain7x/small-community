package com.practice.smallcommunity.interfaces.post;

import com.practice.smallcommunity.application.BoardService;
import com.practice.smallcommunity.application.CategoryService;
import com.practice.smallcommunity.domain.category.Category;
import com.practice.smallcommunity.domain.post.Post;
import com.practice.smallcommunity.domain.post.dto.BoardSearchCond;
import com.practice.smallcommunity.interfaces.PageResponse;
import com.practice.smallcommunity.interfaces.post.dto.BoardResponse;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/posts")
public class BoardController {

    private final CategoryService categoryService;
    private final BoardService boardService;
    private final PostMapper mapper;

    @GetMapping
    public PageResponse<BoardResponse> findPosts(@RequestParam String categoryCode,
        @RequestParam(required = false) @Length(min = 2) String title, Pageable pageable) {
        Category findCategory = categoryService.findOne(categoryCode);
        BoardSearchCond cond = BoardSearchCond.builder()
            .categoryId(findCategory.getId())
            .title(title)
            .build();

        Page<Post> posts = boardService.searchPostsInCategory(cond, pageable);

        return PageResponse.Ok(posts.map(mapper::toBoard));
    }
}
