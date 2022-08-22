package com.practice.smallcommunity.domain.member;

import java.util.Objects;
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

/**
 * 회원 권한 엔티티입니다.
 *
 * 회원 엔티티와 권한 엔티티의 N:M 관계를 N:1로 만들기 위해 존재합니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class MemberRole {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Member member;

    @Builder
    public MemberRole(Long id, Role role, Member member) {
        this.id = id;
        this.role = role;
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
        MemberRole that = (MemberRole) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
