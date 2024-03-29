package com.practice.smallcommunity.reply;

import com.practice.smallcommunity.common.exception.BusinessException;
import com.practice.smallcommunity.common.exception.ErrorCode;
import com.practice.smallcommunity.notification.NotificationService;
import com.practice.smallcommunity.post.domain.Post;
import com.practice.smallcommunity.utils.SecurityUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final NotificationService notificationService;

    /**
     * 답글을 등록하고, 등록된 답글을 반환합니다.
     *  답글이 추가된 게시글의 답글 개수를 증가시킵니다.
     *  답글 추가 알림을 저장합니다.
     * @param reply 답글 정보. 단, id 값은 널이어야 합니다.
     * @return 등록된 답글
     */
    public Reply add(Reply reply) {
        Reply savedReply = replyRepository.save(reply);

        savedReply.getPost().increaseReplyCount();
        notificationService.notifyReply(savedReply.getPost(), savedReply);

        return savedReply;
    }

    /**
     * 답글을 조회합니다.
     *  삭제 상태인 답글은 조회되지 않습니다.
     * @param replyId 답글 ID
     * @return 답글
     * @throws BusinessException
     *          ID가 일치하는 답글이 없거나, 삭제 상태인 경우
     */
    @Transactional(readOnly = true)
    public Reply findEnabledReply(Long replyId) {
        return replyRepository.findByIdAndEnableIsTrue(replyId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_REPLY));
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
     * @param loginId 답글을 수정하려는 현재 회원 ID
     * @param text 새로운 텍스트
     * @return 수정된 답글
     * @throws BusinessException
     *          답글 작성자가 아닌 경우,
     *          ID가 일치하는 답글이 없거나 삭제 상태인 경우
     */
    public Reply update(Long replyId, Long loginId, String text) {
        Reply findReply = findEnabledReply(replyId);
        validateUpdater(findReply, loginId);
        findReply.updateText(text);

        return findReply;
    }

    /**
     * 답글을 삭제 상태로 변경합니다.
     *  관리자는 본인이 작성하지 않은 답글도 삭제할 수 있습니다.
     * @param replyId 답글 ID
     * @param loginId 답글을 수정하려는 현재 회원 ID
     * @throws BusinessException
     *          답글 작성자가 아닌 경우,
     *          ID가 일치하는 답글이 없거나 삭제 상태인 경우
     */
    public void disable(Long replyId, Long loginId) {
        Reply findReply = findEnabledReply(replyId);
        if (!SecurityUtil.isAdmin()) {
            validateUpdater(findReply, loginId);
        }
        findReply.delete();
        findReply.getPost().decreaseReplyCount();
    }

    private void validateUpdater(Reply reply, Long loginId) {
        Long writerId = reply.getWriter().getId();
        if (!writerId.equals(loginId)) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR);
        }
    }
}
