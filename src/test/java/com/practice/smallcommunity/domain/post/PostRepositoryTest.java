package com.practice.smallcommunity.domain.post;

import static org.assertj.core.api.Assertions.assertThat;

import com.practice.smallcommunity.domain.category.Category;
import com.practice.smallcommunity.domain.category.CategoryRepository;
import com.practice.smallcommunity.domain.content.ContentRepository;
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
class PostRepositoryTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ContentRepository contentRepository;

    @Autowired
    PostRepository postRepository;

    Member member = DomainGenerator.createMember("A");
    Category category = DomainGenerator.createCategory("dev", "개발");
    Post post = DomainGenerator.createPost(category, member, "내용");

    @BeforeEach
    void beforeEach() {
        categoryRepository.save(category);
        memberRepository.save(member);
    }

    @Test
    void 저장_및_조회() {
        //when
        postRepository.save(post);
        em.flush();
        em.clear();
        Post findItem = postRepository.findById(post.getId()).orElseThrow();

        //then
        assertThat(post.getId()).isEqualTo(findItem.getId());
        assertThat(post.getTitle()).isEqualTo(findItem.getTitle());
    }

    @Test
    void 여러개_저장_및_조회() {
        //given
        Post post2 = DomainGenerator.createPost(category, member, "내용2");

        //when
        postRepository.save(post);
        postRepository.save(post2);

        long count = postRepository.count();
        List<Post> all = postRepository.findAll();

        //then
        assertThat(count).isEqualTo(2);
        assertThat(all.size()).isEqualTo(2);
    }

    @Test
    void 게시글을_저장하면_컨텐츠도_저장된다() {
        //when
        postRepository.save(post);
        em.flush();
        em.clear();
        Post findItem = postRepository.findById(post.getId()).orElseThrow();
        long contentCount = contentRepository.count();

        //then
        assertThat(contentCount).isEqualTo(1);
        assertThat(post.getContent()).isNotNull();
    }

    @Test
    void 게시글을_저장하면_본문도_저장된다() {
        //when
        postRepository.save(post);
        em.flush();
        em.clear();
        Post findItem = postRepository.findById(post.getId()).orElseThrow();
        Long mainTextCount = em.createQuery("select count(mt) from MainText mt", Long.class)
            .getSingleResult();

        //then
        assertThat(mainTextCount).isEqualTo(1);
        assertThat(post.getContent()).isNotNull();
    }

    @Test
    void 삭제() {
        //given
        postRepository.save(post);

        //when
        Post findItem = postRepository.findById(post.getId()).orElseThrow();
        postRepository.delete(findItem);

        //then
        assertThat(postRepository.count()).isEqualTo(0);
    }
}