package com.practice.smallcommunity.domain.member;

import com.practice.smallcommunity.domain.BaseTimeEntity;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
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

    @Column(length = 15, nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private LocalDateTime lastPasswordChange;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private final Set<MemberRole> memberRoles = new HashSet<>();

    @Builder
    public Member(Long id, String username, String email, String password) {
        this.id = id;
        this.username = username;
        this.email = email;

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

    public void addRole(Role role) {
        MemberRole memberRole = MemberRole.builder()
            .role(role)
            .member(this)
            .build();

        memberRoles.add(memberRole);
    }

    public void removeRole(Role role) {
        memberRoles.removeIf(item -> item.getRole().equals(role));
    }
}
