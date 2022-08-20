package com.practice.smallcommunity.repository.member;

import static org.assertj.core.api.Assertions.assertThat;

import com.practice.smallcommunity.domain.member.Role;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DataJpaTest
class RoleRepositoryTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    RoleRepository roleRepository;

    Role role = Role.builder()
        .name("ROLE_USER")
        .desc("사용자 권한")
        .build();

    @Test
    void 저장_및_조회() {
        //when
        roleRepository.save(role);
        em.flush();
        em.clear();
        Role findItem = roleRepository.findById(role.getId()).orElseThrow();

        //then
        assertThat(role.getId()).isEqualTo(findItem.getId());
        assertThat(role.getName()).isEqualTo(findItem.getName());
    }

    @Test
    void 여러개_저장_및_조회() {
        //given
        Role role2 = Role.builder()
            .name("ROLE_ADMIN")
            .desc("관리자 권한")
            .build();

        //when
        roleRepository.save(role);
        roleRepository.save(role2);

        long count = roleRepository.count();
        List<Role> all = roleRepository.findAll();

        //then
        assertThat(count).isEqualTo(2);
        assertThat(all.size()).isEqualTo(2);
    }

    @Test
    void 삭제() {
        //given
        roleRepository.save(role);

        //when
        Role findItem = roleRepository.findById(role.getId()).orElseThrow();
        roleRepository.delete(findItem);

        //then
        assertThat(roleRepository.count()).isEqualTo(0);
    }
}