package com.practice.smallcommunity.interfaces.member;

import com.practice.smallcommunity.domain.post.Post;
import com.practice.smallcommunity.domain.post.PostRepository;
import com.practice.smallcommunity.domain.reply.Reply;
import com.practice.smallcommunity.domain.reply.ReplyRepository;
import com.practice.smallcommunity.interfaces.CurrentUser;
import com.practice.smallcommunity.interfaces.PageResponse;
import com.practice.smallcommunity.interfaces.member.dto.MemberContentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/members")
public class MemberContentController {

    private final PostRepository postRepository;
    private final ReplyRepository replyRepository;
    private final MemberMapper mapper;

    @GetMapping("/posts")
    public PageResponse<MemberContentResponse> findPosts(@CurrentUser Long loginId, Pageable pageable) {
        Page<Post> posts = postRepository.findPostsByWriter(loginId, pageable);

        return PageResponse.Ok(posts.map(mapper::toContentResponse));
    }

    @GetMapping("/replies")
    public PageResponse<MemberContentResponse> findReplies(@CurrentUser Long loginId, Pageable pageable) {
        Page<Reply> replies = replyRepository.findByWriterFetchPost(loginId, pageable);

        return PageResponse.Ok(replies.map(mapper::toContentResponse));
    }
}
