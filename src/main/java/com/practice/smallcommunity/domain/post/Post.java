package com.practice.smallcommunity.domain.post;

import com.practice.smallcommunity.domain.BaseTimeEntity;
import com.practice.smallcommunity.domain.board.Board;
import com.practice.smallcommunity.domain.member.Member;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
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

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private boolean isNotice;

    private int views;

    private int votes;

    @Builder
    public Post(Long id, Board board, Member writer, String title, String content, boolean isNotice) {
        this.id = id;
        this.board = board;
        this.writer = writer;
        this.nickname = writer.getNickname();
        this.title = title;
        this.content = content;
        this.isNotice = isNotice;
    }
}
