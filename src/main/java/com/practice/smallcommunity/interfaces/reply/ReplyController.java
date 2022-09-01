package com.practice.smallcommunity.interfaces.reply;

import com.practice.smallcommunity.application.MemberService;
import com.practice.smallcommunity.application.PostService;
import com.practice.smallcommunity.application.ReplyService;
import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.domain.post.Post;
import com.practice.smallcommunity.domain.reply.Reply;
import com.practice.smallcommunity.interfaces.CollectionResponse;
import com.practice.smallcommunity.interfaces.CurrentUser;
import com.practice.smallcommunity.interfaces.reply.dto.ReplyAddRequest;
import com.practice.smallcommunity.interfaces.reply.dto.ReplyResponse;
import com.practice.smallcommunity.interfaces.reply.dto.ReplyUpdateRequest;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
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
@RequestMapping("/api/v1")
public class ReplyController {

    private final MemberService memberService;
    private final PostService postService;
    private final ReplyService replyService;
    private final ReplyMapper mapper;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/posts/{postId}/replies")
    public void add(@PathVariable Long postId, @Valid @RequestBody ReplyAddRequest dto) {
        Post post = postService.findPost(postId);
        Member writer = memberService.findByUserId(dto.getMemberId());
        Reply reply = Reply.builder()
            .post(post)
            .writer(writer)
            .text(dto.getText())
            .build();

        replyService.add(reply);
    }

    @GetMapping("/posts/{postId}/replies")
    public CollectionResponse<ReplyResponse> findReplies(@PathVariable Long postId) {
        Post post = postService.findPost(postId);
        List<Reply> replies = replyService.findRepliesOnPost(post);
        List<ReplyResponse> result = replies.stream()
            .map(mapper::toResponse)
            .collect(Collectors.toList());

        return new CollectionResponse<>(result);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/replies/{replyId}")
    public void update(@PathVariable Long replyId,
        @CurrentUser Long loginId,
        @NotBlank @RequestBody ReplyUpdateRequest dto) {
        replyService.update(replyId, loginId, dto.getText());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/replies/{replyId}")
    public void delete(@PathVariable Long replyId, @CurrentUser Long loginId) {
        replyService.disable(replyId, loginId);
    }
}
