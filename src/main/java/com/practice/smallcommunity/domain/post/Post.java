package com.practice.smallcommunity.domain.post;

import com.practice.smallcommunity.domain.BaseTimeEntity;
import com.practice.smallcommunity.domain.board.Board;
import com.practice.smallcommunity.domain.content.Content;
import com.practice.smallcommunity.domain.member.Member;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Member writer;

    @Column(nullable = false)
    private String nickname;

    @Column(length = 20, nullable = false)
    private String title;

    @OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL, orphanRemoval = true)
    private Content content;

    private boolean notice;

    private boolean enable;

    private int views;

    private int votes;

    @Builder
    public Post(Board board, Member writer, String title, Content content) {
        this.board = board;
        this.writer = writer;
        this.nickname = writer.getNickname();
        this.title = title;
        this.content = content;
        enable = true;
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateContent(String text) {
        this.content.updateText(text);
    }

    public void setNotice(boolean notice) {
        this.notice = notice;
    }

    public void increaseViewCount() {
        views++;
    }

    public void vote(boolean positive) {
        votes += positive ? 1 : -1;
    }

    public void delete() {
        enable = false;
    }
}
