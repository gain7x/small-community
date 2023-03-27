package com.practice.smallcommunity.notification.interfaces.dto;

import com.practice.smallcommunity.notification.domain.NotificationType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponse {

    private long id;
    private String sender;
    private Long postId;
    private NotificationType type;
    private boolean read;
    private LocalDateTime createdDate;
}
