package com.practice.smallcommunity.post.domain;

import com.practice.smallcommunity.member.domain.Member;
import javax.persistence.Column;
import javax.persistence.Entity;
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
public class MainText {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "main_text_seq_gen")
    @SequenceGenerator(name = "main_text_seq_gen", sequenceName = "main_text_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Member writer;

    @Column(columnDefinition = "TEXT")
    private String text;

    @Builder
    public MainText(Member writer, String text) {
        this.writer = writer;
        this.text = text;
    }

    public void updateText(String text) {
        this.text = text;
    }
}
