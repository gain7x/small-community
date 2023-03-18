package com.practice.smallcommunity.post.domain;

import com.practice.smallcommunity.common.domain.BaseTimeEntity;
import com.practice.smallcommunity.category.Category;
import com.practice.smallcommunity.content.domain.Content;
import com.practice.smallcommunity.member.domain.Member;
import com.practice.smallcommunity.reply.Reply;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 게시글 엔티티입니다.
 *  본문( MainText )은 조회 빈도가 상대적으로 낮기 때문에 JPA 사용 편의를 위해 지연로딩으로 분리합니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "post_seq_gen")
    @SequenceGenerator(name = "post_seq_gen", sequenceName = "post_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Member writer;

    @Column(nullable = false)
    private String nickname;

    @Column(length = 75, nullable = false)
    private String title;

    @OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL, orphanRemoval = true)
    private Content content;

    @OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL, orphanRemoval = true)
    private MainText mainText;

    @OneToOne(fetch = FetchType.LAZY)
    private Reply acceptedReply;

    private boolean enable;

    private int views;

    private int replyCount;

    private int votes;

    @Builder
    public Post(Category category, Member writer, String title, String text) {
        this.category = category;
        this.writer = writer;
        this.nickname = writer.getNickname();
        this.title = title;
        this.content = Content.builder()
            .member(writer)
            .build();
        this.mainText = MainText.builder()
            .writer(writer)
            .text(text)
            .build();

        enable = true;
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateContent(String text) {
        this.mainText.updateText(text);
    }

    public void increaseViewCount() {
        views++;
    }

    public void increaseReplyCount() {
        replyCount++;
    }

    public void decreaseReplyCount() {
        replyCount--;
    }

    public void vote(boolean positive) {
        votes += positive ? 1 : -1;
    }

    public void accept(Reply reply) {
        acceptedReply = reply;
    }

    public void delete() {
        enable = false;
    }
}
