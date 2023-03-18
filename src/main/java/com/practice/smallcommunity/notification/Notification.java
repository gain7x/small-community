package com.practice.smallcommunity.notification;

import com.practice.smallcommunity.common.domain.BaseTimeEntity;
import com.practice.smallcommunity.member.domain.Member;
import com.practice.smallcommunity.post.domain.Post;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Notification extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notification_seq_gen")
    @SequenceGenerator(name = "notification_seq_gen", sequenceName = "notification_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Member receiver;

    private String sender;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Post relatedPost;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private boolean isRead;

    @Builder
    public Notification(Member receiver, String sender, Post relatedPost, NotificationType type) {
        this.receiver = receiver;
        this.sender = sender;
        this.relatedPost = relatedPost;
        this.type = type;
    }

    public void read() {
        isRead = true;
    }
}
