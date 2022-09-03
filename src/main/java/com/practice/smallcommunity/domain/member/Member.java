package com.practice.smallcommunity.domain.member;

import com.practice.smallcommunity.domain.BaseTimeEntity;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 회원 엔티티입니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 12, nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    private LocalDateTime lastPasswordChange;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRole memberRole;

    @Builder
    public Member(String email, String password, String nickname, MemberRole memberRole) {
        this.email = email;
        this.nickname = nickname;
        this.memberRole = memberRole;

        changePassword(password);
    }

    /**
     * 암호를 변경합니다. 이때, 암호 변경일도 갱신됩니다.
     * @param password 새로운 암호
     */
    public void changePassword(String password) {
        this.password = password;
        lastPasswordChange = LocalDateTime.now();
    }

    public void changeMemberRole(MemberRole memberRole) {
        this.memberRole = memberRole;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Member member = (Member) o;
        return id.equals(member.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
