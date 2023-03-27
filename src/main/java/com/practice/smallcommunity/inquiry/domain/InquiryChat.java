package com.practice.smallcommunity.inquiry.domain;

import com.practice.smallcommunity.common.domain.BaseTimeEntity;
import com.practice.smallcommunity.member.domain.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class InquiryChat extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inquiry_chat_seq_gen")
    @SequenceGenerator(name = "inquiry_chat_seq_gen", sequenceName = "inquiry_chat_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member inquirer;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member sender;

    @Column(nullable = false)
    private String content;

    @Builder
    public InquiryChat(Member inquirer, Member sender, String content) {
        this.inquirer = inquirer;
        this.sender = sender;
        this.content = content;
    }
}
