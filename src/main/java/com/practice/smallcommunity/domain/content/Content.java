package com.practice.smallcommunity.domain.content;

import com.practice.smallcommunity.domain.member.Member;
import java.util.Objects;
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

/**
 * 컨텐츠 엔티티입니다.
 *  게시글, 댓글, 질문, 답글과 같은 엔티티의 공통 항목 관리용 중간 테이블로 사용합니다.
 *      -공통항목: 투표 내역, 첨부파일
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "content_seq_gen")
    @SequenceGenerator(name = "content_seq_gen", sequenceName = "content_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Member member;

    @Builder
    public Content(Member member) {
        this.member = member;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Content content = (Content) o;
        return id.equals(content.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
