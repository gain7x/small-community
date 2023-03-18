package com.practice.smallcommunity.content.domain;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class VoteHistoryId implements Serializable {

    private static final long serialVersionUID = -9038224688431065738L;

    private Long memberId;

    private Long contentId;

    public VoteHistoryId(Long memberId, Long contentId) {
        this.memberId = memberId;
        this.contentId = contentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        VoteHistoryId that = (VoteHistoryId) o;
        return memberId.equals(that.memberId) && contentId.equals(that.contentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberId, contentId);
    }
}
