package com.practice.smallcommunity.domain.auth;

import com.practice.smallcommunity.domain.member.Member;
import java.time.LocalDateTime;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 로그인 엔티티입니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Login {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "site_login_seq_gen", sequenceName = "site_login_seq")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL, orphanRemoval = true)
    private Member member;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private LocalDateTime lastPasswordChange;

    @Column(nullable = false)
    private boolean emailVerified;

    @Builder
    public Login(Member member, String password) {
        this.member = member;

        changePassword(password);
    }

    /**
     * 암호를 변경하고, 암호 변경일을 갱신합니다.
     * @param password 새로운 암호
     */
    public void changePassword(String password) {
        this.password = password;
        lastPasswordChange = LocalDateTime.now();
    }

    public void verifyEmail() {
        this.emailVerified = true;
    }
}
