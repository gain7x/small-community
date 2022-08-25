package com.practice.smallcommunity.domain.content;

import com.practice.smallcommunity.domain.member.Member;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class VoteHistory {

    @EmbeddedId
    private VoteHistoryId id;

    @MapsId("memberId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Member voter;

    @MapsId("contentId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Content content;

    @Column(nullable = false)
    private boolean positive;

    @Builder
    public VoteHistory(Member voter, Content content, boolean positive) {
        id = new VoteHistoryId(voter.getId(), content.getId());
        this.voter = voter;
        this.content = content;
        this.positive = positive;
    }
}
