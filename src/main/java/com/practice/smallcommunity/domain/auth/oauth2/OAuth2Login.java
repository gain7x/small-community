package com.practice.smallcommunity.domain.auth.oauth2;

import com.practice.smallcommunity.domain.BaseTimeEntity;
import com.practice.smallcommunity.domain.member.Member;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * OAuth2 로그인 엔티티입니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "oauth2_login")
public class OAuth2Login extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "oauth2_login_seq_gen", sequenceName = "oauth2_login_seq")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL, orphanRemoval = true)
    private Member member;

    // 각 인가 서버에서 자원 소유자를 고유하게 구별하는 식별자입니다.
    private String username;

    @Enumerated(EnumType.STRING)
    private OAuth2Platform platform;

    @Builder
    public OAuth2Login(Member member, String username, OAuth2Platform platform) {
        this.member = member;
        this.username = username;
        this.platform = platform;
    }
}
