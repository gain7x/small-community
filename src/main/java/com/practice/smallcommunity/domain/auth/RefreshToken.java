package com.practice.smallcommunity.domain.auth;

import com.practice.smallcommunity.domain.member.Member;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 리프레시 토큰 엔티티입니다.
 *  차후 기기 별 관리 가능성을 고려하여 회원을 다대일로 매핑합니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class RefreshToken {

    @Id
    private String token;

    @ManyToOne
    private Member member;

    @Builder
    public RefreshToken(String token, Member member) {
        this.token = token;
        this.member = member;
    }
}
