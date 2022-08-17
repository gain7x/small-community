package com.practice.smallcommunity.domain.content;

import com.practice.smallcommunity.domain.BaseTimeEntity;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Content extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String text;

    private int totalVote;

    @Builder
    public Content(Long id, String text, int totalVote) {
        this.id = id;
        this.text = text;
        this.totalVote = totalVote;
    }

    public void vote(boolean positive) {
        totalVote += positive ? 1 : -1;
    }
}
