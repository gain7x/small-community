package com.practice.smallcommunity.interfaces.notification.dto;

import com.practice.smallcommunity.domain.notification.NotificationType;
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
