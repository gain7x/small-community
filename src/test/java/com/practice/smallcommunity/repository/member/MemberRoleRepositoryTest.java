package com.practice.smallcommunity.repository.member;

import static org.assertj.core.api.Assertions.assertThat;

import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.domain.member.MemberRole;
import com.practice.smallcommunity.domain.member.Role;
import com.practice.smallcommunity.domain.member.RoleType;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DataJpaTest
class MemberRoleRepositoryTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    MemberRoleRepository memberRoleRepository;

    Member member = Member.builder()
        .username("userA")
        .password("password")
        .email("userA@mail.com")
        .build();

    Role role = Role.builder()
        .roleType(RoleType.ROLE_USER)
        .desc("사용자 권한")
        .build();

    MemberRole memberRole = MemberRole.builder()
        .role(role)
        .member(member)
        .build();

    @BeforeEach
    void beforeEach() {
        memberRepository.save(member);
        roleRepository.save(role);
    }

    @Test
    void 저장_및_조회() {
        //when
        memberRoleRepository.save(memberRole);
        em.flush();
        em.clear();
        MemberRole findItem = memberRoleRepository.findById(memberRole.getId()).orElseThrow();

        //then
        assertThat(memberRole.getId()).isEqualTo(findItem.getId());
        assertThat(memberRole.getRole().getId()).isEqualTo(findItem.getRole().getId());
    }

    @Test
    void 여러개_저장_및_조회() {
        //given
        Role role2 = Role.builder()
            .roleType(RoleType.ROLE_ADMIN)
            .desc("관리자 권한")
            .build();

        MemberRole memberRole2 = MemberRole.builder()
            .role(role2)
            .member(member)
            .build();

        roleRepository.save(role2);

        //when
        memberRoleRepository.save(memberRole);
        memberRoleRepository.save(memberRole2);

        long count = memberRoleRepository.count();
        List<MemberRole> all = memberRoleRepository.findAll();

        //then
        assertThat(count).isEqualTo(2);
        assertThat(all.size()).isEqualTo(2);
    }

    @Test
    void 삭제() {
        //given
        memberRoleRepository.save(memberRole);

        //when
        MemberRole findItem = memberRoleRepository.findById(memberRole.getId()).orElseThrow();
        memberRoleRepository.delete(findItem);

        //then
        assertThat(memberRoleRepository.count()).isEqualTo(0);
    }

    @Test
    void 회원엔티티에_권한을_추가하면_권한엔티티가_함께_저장된다() {
        //given
        member.addRole(role);

        //when
        long count = memberRoleRepository.count();

        //then
        assertThat(count).isEqualTo(1);
    }

    @Test
    void 회원엔티티에서_권한을_제거하면_권한엔티티가_삭제된다() {
        //given
        member.addRole(role);
        em.flush();
        em.clear();

        Member findMember = memberRepository.findById(this.member.getId()).orElseThrow();
        Set<MemberRole> memberRoles = findMember.getMemberRoles();
        assertThat(memberRoles.size()).isEqualTo(1);

        //when
        findMember.removeRole(role);
        em.flush();
        em.clear();
        List<MemberRole> all = memberRoleRepository.findAll();

        //then
        assertThat(all.size()).isEqualTo(0);
    }
}