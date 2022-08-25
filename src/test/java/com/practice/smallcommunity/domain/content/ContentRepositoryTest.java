package com.practice.smallcommunity.domain.content;

import static org.assertj.core.api.Assertions.assertThat;

import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.domain.member.MemberRepository;
import com.practice.smallcommunity.utils.DomainGenerator;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class ContentRepositoryTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    ContentRepository contentRepository;

    @Autowired
    MemberRepository memberRepository;

    Member member = DomainGenerator.createMember("A");

    Content content = Content.builder()
        .writer(member)
        .text("컨텐츠")
        .build();

    @BeforeEach
    void setUp() {
        memberRepository.save(member);
    }

    @Test
    void 저장_및_조회() {
        //when
        contentRepository.save(content);
        em.flush();
        em.clear();
        Content findItem = contentRepository.findById(content.getId()).orElseThrow();

        //then
        assertThat(content.getId()).isEqualTo(findItem.getId());
        assertThat(content.getText()).isEqualTo(findItem.getText());
    }

    @Test
    void 여러개_저장_및_조회() {
        //given
        Content content2 = Content.builder()
            .writer(member)
            .text("컨텐츠2")
            .build();

        //when
        contentRepository.save(content);
        contentRepository.save(content2);

        long count = contentRepository.count();
        List<Content> all = contentRepository.findAll();

        //then
        assertThat(count).isEqualTo(2);
        assertThat(all.size()).isEqualTo(2);
    }

    @Test
    void 삭제() {
        //given
        contentRepository.save(content);

        //when
        Content findItem = contentRepository.findById(content.getId()).orElseThrow();
        contentRepository.delete(findItem);

        //then
        assertThat(contentRepository.count()).isEqualTo(0);
    }
}