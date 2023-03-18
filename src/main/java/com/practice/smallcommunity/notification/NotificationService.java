package com.practice.smallcommunity.notification;

import com.practice.smallcommunity.common.exception.BusinessException;
import com.practice.smallcommunity.common.exception.ErrorCode;
import com.practice.smallcommunity.member.Member;
import com.practice.smallcommunity.post.domain.Post;
import com.practice.smallcommunity.reply.Reply;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public Notification findOne(Long notificationId) {
        return notificationRepository.findById(notificationId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_NOTIFICATION));
    }

    /**
     * 답글 추가 알림을 저장합니다.
     * @param post 대상 게시글
     * @param reply 추가되는 답글
     */
    public void notifyReply(Post post, Reply reply) {
        if (!isValidNotification(post.getWriter(), reply.getWriter())) {
            return;
        }

        Notification notification = Notification.builder()
            .receiver(post.getWriter())
            .sender(reply.getNickname())
            .type(NotificationType.REPLY)
            .relatedPost(post)
            .build();

        notificationRepository.save(notification);
    }

    /**
     * 투표 알림을 저장합니다.
     * @param receiver 알림 수신자
     * @param sender 송신자( 투표자 )
     * @param relatedPost 관련 게시글
     */
    public void notifyVote(Member receiver, Member sender, Post relatedPost) {
        if (!isValidNotification(receiver, sender)) {
            return;
        }

        Notification notification = Notification.builder()
            .receiver(receiver)
            .sender(sender.getNickname())
            .type(NotificationType.VOTE)
            .relatedPost(relatedPost)
            .build();

        notificationRepository.save(notification);
    }

    /**
     * 알림을 읽음 상태로 변경합니다.
     * @param loginId 현재 로그인 회원 ID
     * @param notificationId 알림 ID
     * @throws BusinessException
     *          알림을 찾을 수 없는 경우, 현재 로그인 회원이 알림 수신자가 아닌 경우
     */
    public void read(Long loginId, Long notificationId) {
        Notification findNotification = findOne(notificationId);
        if (!findNotification.getReceiver().getId().equals(loginId)) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR);
        }

        findNotification.read();
    }

    /**
     * 읽지 않은 알림을 모두 읽음 처리합니다.
     * @param loginId 현재 로그인 회원 ID
     */
    public void readAllUnreadNotifications(Long loginId) {
        notificationRepository.readAllUnreadNotifications(loginId);
    }

    private boolean isValidNotification(Member receiver, Member sender) {
        return !receiver.getId().equals(sender.getId());
    }
}
