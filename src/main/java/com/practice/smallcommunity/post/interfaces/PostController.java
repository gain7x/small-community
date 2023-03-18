package com.practice.smallcommunity.post.interfaces;

import com.practice.smallcommunity.category.CategoryService;
import com.practice.smallcommunity.content.VoteService;
import com.practice.smallcommunity.common.exception.BusinessException;
import com.practice.smallcommunity.common.exception.ErrorCode;
import com.practice.smallcommunity.member.application.MemberService;
import com.practice.smallcommunity.post.application.PostService;
import com.practice.smallcommunity.post.application.dto.PostDto;
import com.practice.smallcommunity.reply.ReplyService;
import com.practice.smallcommunity.category.Category;
import com.practice.smallcommunity.member.domain.Member;
import com.practice.smallcommunity.member.domain.MemberRole;
import com.practice.smallcommunity.post.domain.Post;
import com.practice.smallcommunity.reply.Reply;
import com.practice.smallcommunity.common.interfaces.dto.BaseResponse;
import com.practice.smallcommunity.common.interfaces.CurrentUser;
import com.practice.smallcommunity.content.interfaces.dto.VoteRequest;
import com.practice.smallcommunity.content.interfaces.dto.VoteResponse;
import com.practice.smallcommunity.post.interfaces.dto.AcceptReplyRequest;
import com.practice.smallcommunity.post.interfaces.dto.PostRequest;
import com.practice.smallcommunity.post.interfaces.dto.PostResponse;
import com.practice.smallcommunity.post.interfaces.dto.PostSimpleResponse;
import com.practice.smallcommunity.post.interfaces.dto.PostUpdateRequest;
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
    private final ReplyService replyService;
    private final PostMapper mapper;
    private final VoteService voteService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public BaseResponse<PostSimpleResponse> write(@CurrentUser Long loginId, @Valid @RequestBody PostRequest dto) {
        Category category = categoryService.findEnableCategory(dto.getCategoryCode());
        Member member = memberService.findByUserId(loginId);

        if (category.isCudAdminOnly() && !member.getMemberRole().equals(MemberRole.ADMIN)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

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
        Post findPost = postService.viewPost(postId);
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

    @PostMapping("/{postId}/vote")
    public BaseResponse<VoteResponse> vote(@PathVariable Long postId, @CurrentUser Long loginId,
        @Valid @RequestBody VoteRequest dto) {
        Member voter = memberService.findByUserId(loginId);
        boolean result = voteService.votePost(postId, voter, dto.getPositive());

        return BaseResponse.Ok(VoteResponse.builder()
            .voted(result)
            .build());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/{postId}/accept")
    public void accept(@PathVariable Long postId, @CurrentUser Long loginId,
        @Valid @RequestBody AcceptReplyRequest dto) {
        Reply targetReply = replyService.findEnabledReply(dto.getReplyId());
        postService.accept(postId, loginId, targetReply);
    }
}
