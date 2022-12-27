package com.practice.smallcommunity.domain.member;

import com.practice.smallcommunity.domain.BaseTimeEntity;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "member_seq_gen")
    @SequenceGenerator(name = "member_seq_gen", sequenceName = "member_seq")
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(length = 12, nullable = false, unique = true)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRole memberRole;

    @Column(nullable = false)
    private boolean withdrawal;

    private LocalDateTime withdrawnDate;

    @Builder
    public Member(String email, String nickname, MemberRole memberRole) {
        this.email = email;
        this.nickname = nickname;
        this.memberRole = memberRole;
    }

    /**
     * 별명을 변경합니다.
     * @param nickname 새로운 별명
     */
    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * 회원을 탈퇴 상태로 변경합니다.
     */
    public void withdraw() {
        this.withdrawal = true;
        withdrawnDate = LocalDateTime.now();
    }
}
