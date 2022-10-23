package com.practice.smallcommunity.domain.notification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("select n from Notification n where n.receiver.id = :receiverId and n.createdDate > current_date - 7")
    Page<Notification> findValidNotifications(@Param("receiverId") Long receiverId, Pageable pageable);
}
