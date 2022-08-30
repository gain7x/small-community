package com.practice.smallcommunity.application;

import com.practice.smallcommunity.application.exception.ValidationError;
import com.practice.smallcommunity.application.exception.ValidationErrorException;
import com.practice.smallcommunity.application.exception.ValidationErrorStatus;
import com.practice.smallcommunity.domain.post.Post;
import com.practice.smallcommunity.domain.reply.Reply;
import com.practice.smallcommunity.domain.reply.ReplyRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class ReplyService {

    private final ReplyRepository replyRepository;

    /**
     * 답글을 등록하고, 등록된 답글을 반환합니다.
     * @param reply 답글 정보. 단, id 값은 널이어야 합니다.
     * @return 등록된 답글
     */
    public Reply add(Reply reply) {
        return replyRepository.save(reply);
    }

    /**
     * 답글을 조회합니다.
     *  삭제 상태인 답글은 조회되지 않습니다.
     * @param replyId 답글 ID
     * @return 답글
     * @throws ValidationErrorException
     *          ID가 일치하는 답글이 없거나, 삭제 상태인 경우
     */
    @Transactional(readOnly = true)
    public Reply findEnabledReply(Long replyId) {
        return replyRepository.findByIdAndEnableIsTrue(replyId)
            .orElseThrow(() -> new ValidationErrorException("답글을 찾을 수 없습니다.",
            ValidationError.of(ValidationErrorStatus.NOT_FOUND, "replyId")));
    }

    /**
     * 게시글의 답글 목록을 조회합니다.
     *  삭제 상태인 답글은 조회되지 않습니다.
     * @param post 게시글
     * @return 답글 목록
     */
    @Transactional(readOnly = true)
    public List<Reply> findRepliesOnPost(Post post) {
        return replyRepository.findByPostAndEnableIsTrue(post);
    }

    /**
     * 답글을 수정하고, 성공하면 수정된 답글을 반환합니다.
     * @param replyId 답글 ID
     * @param text 새로운 텍스트
     * @return 수정된 답글
     * @throws ValidationErrorException
     * ID가 일치하는 답글이 없거나, 삭제 상태인 경우
     */
    public Reply update(Long replyId, String text) {
        Reply reply = findEnabledReply(replyId);
        reply.updateText(text);
        return reply;
    }

    /**
     * 답글을 삭제 상태로 변경합니다.
     * @param replyId 답글 ID
     * @throws ValidationErrorException
     *          ID가 일치하는 답글이 없거나, 삭제 상태인 경우
     */
    public void disable(Long replyId) {
        Reply reply = findEnabledReply(replyId);
        reply.delete();
    }
}
