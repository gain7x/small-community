package com.practice.smallcommunity.content;

import com.practice.smallcommunity.notification.NotificationService;
import com.practice.smallcommunity.reply.ReplyService;
import com.practice.smallcommunity.post.application.PostService;
import com.practice.smallcommunity.member.Member;
import com.practice.smallcommunity.post.domain.Post;
import com.practice.smallcommunity.reply.Reply;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class VoteService {

    private final PostService postService;
    private final ReplyService replyService;
    private final VoteHistoryService voteHistoryService;
    private final NotificationService notificationService;

    /**
     * 답글을 투표하고, 투표 알림을 저장합니다.
     * @param replyId  답글 ID
     * @param voter    투표자
     * @param positive 긍정 여부
     * @return 투표수에 변화 발생 시 TRUE, 그대로면 FALSE
     * @see VoteHistoryService#addVoteHistory
     */
    public boolean voteReply(Long replyId, Member voter, boolean positive) {
        Reply findReply = replyService.findEnabledReply(replyId);
        boolean voted = voteHistoryService.addVoteHistory(voter, findReply.getContent(), positive);
        if (voted) {
            findReply.vote(positive);
            notificationService.notifyVote(findReply.getWriter(), voter, findReply.getPost());
        }

        return voted;
    }

    /**
     * 게시글을 투표하고, 투표 알림을 저장합니다.
     * @param postId   게시글 ID
     * @param voter    투표자
     * @param positive 긍정 여부
     * @return 투표수에 변화 발생 시 TRUE, 그대로면 FALSE
     * @see VoteHistoryService#addVoteHistory
     */
    public boolean votePost(Long postId, Member voter, boolean positive) {
        Post findPost = postService.findPost(postId);
        boolean voted = voteHistoryService.addVoteHistory(voter, findPost.getContent(), positive);
        if (voted) {
            findPost.vote(positive);
            notificationService.notifyVote(findPost.getWriter(), voter, findPost);
        }

        return voted;
    }
}
