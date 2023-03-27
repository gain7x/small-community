package com.practice.smallcommunity.notification.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.practice.smallcommunity.common.exception.BusinessException;
import com.practice.smallcommunity.common.exception.ErrorCode;
import com.practice.smallcommunity.member.domain.Member;
import com.practice.smallcommunity.notification.NotificationService;
import com.practice.smallcommunity.notification.domain.Notification;
import com.practice.smallcommunity.notification.domain.NotificationRepository;
import com.practice.smallcommunity.notification.domain.NotificationType;
import com.practice.smallcommunity.post.domain.Post;
import com.practice.smallcommunity.reply.Reply;
import com.practice.smallcommunity.testutils.DomainGenerator;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    NotificationRepository notificationRepository;

    NotificationService notificationService;

    @Spy
    Member receiver = DomainGenerator.createMember("A");

    Member sender = DomainGenerator.createMember("B");

    @BeforeEach
    void setUp() {
        notificationService = new NotificationService(notificationRepository);
    }

    @Test
    void 알림을_조회한다() {
        //given
        Notification notification = DomainGenerator.createNotification(receiver, null);

        when(notificationRepository.findById(1L))
            .thenReturn(Optional.of(notification));

        //when
        Notification result = notificationService.findOne(1L);

        //then
        assertThat(result).isNotNull();
    }

    @Test
    void 알림_조회_시_ID가_일치하는_알림이_없으면_예외를_던진다() {
        //given
        when(notificationRepository.findById(1L))
            .thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> notificationService.findOne(1L))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_FOUND_NOTIFICATION);
    }

    @Test
    void 답글_알림을_저장한다() {
        //when
        when(receiver.getId()).thenReturn(1L);
        Post post = DomainGenerator.createPost(null, receiver, "");
        Reply reply = DomainGenerator.createReply(post, sender, "");

        assertThatNoException().isThrownBy(
            () -> notificationService.notifyReply(post, reply));

        //then
        verify(notificationRepository, times(1)).save(
            argThat(x -> x.getType().equals(NotificationType.REPLY)));
    }

    @Test
    void 투표_알림을_저장한다() {
        //when
        when(receiver.getId()).thenReturn(1L);
        assertThatNoException().isThrownBy(
            () -> notificationService.notifyVote(receiver, sender, null));

        //then
        verify(notificationRepository, times(1)).save(
            argThat(x -> x.getType().equals(NotificationType.VOTE)));
    }

    @Test
    void 모든_알림을_읽음_상태로_변경한다() {
        //when
        notificationService.readAllUnreadNotifications(1L);

        //then
        verify(notificationRepository, times(1)).readAllUnreadNotifications(1L);
    }

    @Test
    void 알림을_읽음_상태로_변경한다() {
        //given
        Notification notification = DomainGenerator.createNotification(receiver, null);

        when(receiver.getId()).thenReturn(1L);
        when(notificationRepository.findById(1L))
            .thenReturn(Optional.of(notification));

        //when
        notificationService.read(1L, 1L);

        //then
        assertThat(notification.isRead()).isTrue();
    }

    @Test
    void 알림을_읽음_상태로_변경할_때_해당_회원에게_송신된_알림이_아니면_예외를_던진다() {
        //given
        Notification notification = DomainGenerator.createNotification(receiver, null);

        when(receiver.getId()).thenReturn(1L);
        when(notificationRepository.findById(1L))
            .thenReturn(Optional.of(notification));

        //when
        assertThatThrownBy(() -> notificationService.read(2L, 1L))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.VALIDATION_ERROR);
    }
}