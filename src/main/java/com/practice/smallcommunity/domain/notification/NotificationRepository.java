package com.practice.smallcommunity.domain.notification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * 최근 7일 내에 발생한 알림 목록을 반환합니다.
     * @param receiverId 알림 수신자( 회원 ) ID
     * @param pageable 페이징 정보
     * @return 최근 7일 내에 발생한 알림 목록
     */
    @Query("select n from Notification n where n.receiver.id = :receiverId and n.createdDate > current_date - 7")
    Page<Notification> findRecentNotifications(@Param("receiverId") Long receiverId, Pageable pageable);


    /**
     * 읽지 않은 알림을 모두 읽음 처리합니다.
     *  수정일을 현재 일시로 갱신합니다.
     * @param receiverId 알림 수신자( 회원 ) ID
     */
    @Modifying
    @Query("update Notification n set n.isRead = true, n.lastModifiedDate = current_timestamp where n.receiver.id = :receiverId and n.isRead = false")
    void readAllUnreadNotifications(@Param("receiverId") Long receiverId);
}
