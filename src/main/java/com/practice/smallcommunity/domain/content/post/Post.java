package com.practice.smallcommunity.domain.content.post;

import com.practice.smallcommunity.domain.BaseTimeEntity;
import com.practice.smallcommunity.domain.content.Content;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(length = 20, nullable = false)
    private String title;

    private boolean isNotice;

    @JoinColumn(nullable = false)
    @OneToOne(fetch = FetchType.LAZY)
    private Content content;

    @Builder
    public Post(Long id, String title, boolean isNotice, Content content) {
        this.id = id;
        this.title = title;
        this.isNotice = isNotice;
        this.content = content;
    }
}
