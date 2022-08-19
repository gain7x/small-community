package com.practice.smallcommunity.domain.member;

import com.practice.smallcommunity.domain.BaseTimeEntity;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(length = 15, nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private LocalDateTime lastPasswordChange;

    @Builder
    public Member(Long id, String username, String email, String password) {
        this.id = id;
        this.username = username;
        this.email = email;

        changePassword(password);
    }

    public void changePassword(String password) {
        this.password = password;
        lastPasswordChange = LocalDateTime.now();
    }
}
