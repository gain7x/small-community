package com.practice.smallcommunity.notification.interfaces;

import com.practice.smallcommunity.notification.NotificationService;
import com.practice.smallcommunity.notification.domain.Notification;
import com.practice.smallcommunity.notification.domain.NotificationRepository;
import com.practice.smallcommunity.common.interfaces.CurrentUser;
import com.practice.smallcommunity.common.interfaces.dto.PageResponse;
import com.practice.smallcommunity.notification.interfaces.dto.NotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;
    private final NotificationMapper mapper;

    @GetMapping
    public PageResponse<NotificationResponse> find(@CurrentUser Long loginId, Pageable pageable) {
        Page<Notification> notificationPage = notificationRepository.findRecentNotifications(loginId,
            pageable);

        return PageResponse.Ok(notificationPage.map(mapper::toResponse));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping
    public void readAll(@CurrentUser Long loginId) {
        notificationService.readAllUnreadNotifications(loginId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/{notificationId}")
    public void read(@CurrentUser Long loginId, @PathVariable Long notificationId) {
        notificationService.read(loginId, notificationId);
    }
}
