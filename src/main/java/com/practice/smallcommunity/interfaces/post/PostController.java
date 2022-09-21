package com.practice.smallcommunity.interfaces.post;

import com.practice.smallcommunity.application.CategoryService;
import com.practice.smallcommunity.application.MemberService;
import com.practice.smallcommunity.application.PostService;
import com.practice.smallcommunity.application.dto.PostDto;
import com.practice.smallcommunity.domain.category.Category;
import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.domain.post.Post;
import com.practice.smallcommunity.interfaces.BaseResponse;
import com.practice.smallcommunity.interfaces.CurrentUser;
import com.practice.smallcommunity.interfaces.post.dto.PostRequest;
import com.practice.smallcommunity.interfaces.post.dto.PostResponse;
import com.practice.smallcommunity.interfaces.post.dto.PostSimpleResponse;
import com.practice.smallcommunity.interfaces.post.dto.PostUpdateRequest;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

    private final CategoryService categoryService;
    private final MemberService memberService;
    private final PostService postService;
    private final PostMapper mapper;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public BaseResponse<PostSimpleResponse> write(@CurrentUser Long loginId, @Valid @RequestBody PostRequest dto) {
        Category category = categoryService.findOne(dto.getCategoryCode());
        Member member = memberService.findByUserId(loginId);
        PostDto postDto = PostDto.builder()
            .title(dto.getTitle())
            .text(dto.getText())
            .build();

        Post result = postService.write(category, member, postDto);

        log.info("Post was written. category: {}-{}, postId: {}, nickname: {}, title: {}",
            category.getId(), category.getCode(), result.getId(), member.getNickname(),
            result.getTitle());

        return BaseResponse.Ok(PostSimpleResponse.builder()
            .postId(result.getId())
            .build());
    }

    @GetMapping("/{postId}")
    public BaseResponse<PostResponse> find(@PathVariable Long postId) {
        Post findPost = postService.findPostFetchMainText(postId);
        return BaseResponse.Ok(mapper.toResponse(findPost));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/{postId}")
    public void edit(@PathVariable Long postId,
        @CurrentUser Long loginId,
        @Valid @RequestBody PostUpdateRequest dto) {
        PostDto postDto = PostDto.builder()
            .title(dto.getTitle())
            .text(dto.getText())
            .build();

        postService.update(postId, loginId, postDto);

        log.info("Post was edited. postId: {}", postId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{postId}")
    public void disable(@PathVariable Long postId, @CurrentUser Long loginId) {
        postService.disable(postId, loginId);

        log.info("Post was disabled. postId: {}", postId);
    }
}
