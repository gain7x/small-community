package com.practice.smallcommunity.reply.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.practice.smallcommunity.category.Category;
import com.practice.smallcommunity.category.CategoryRepository;
import com.practice.smallcommunity.content.domain.ContentRepository;
import com.practice.smallcommunity.member.Member;
import com.practice.smallcommunity.member.MemberRepository;
import com.practice.smallcommunity.post.domain.Post;
import com.practice.smallcommunity.post.domain.PostRepository;
import com.practice.smallcommunity.reply.Reply;
import com.practice.smallcommunity.reply.ReplyRepository;
import com.practice.smallcommunity.testutils.DomainGenerator;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.assertj.core.api.Assertions;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@DataJpaTest
class ReplyRepositoryTest {

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

    @Autowired
    ReplyRepository replyRepository;

    Member member = DomainGenerator.createMember("A");
    Category category = DomainGenerator.createCategory("dev", "개발");
    Post post = DomainGenerator.createPost(category, member, "내용");
    Reply reply = DomainGenerator.createReply(post, member, "답글");

    @BeforeEach
    void beforeEach() {
        categoryRepository.save(category);
        memberRepository.save(member);
        postRepository.save(post);
    }

    @Test
    void 저장_및_조회() {
        //when
        replyRepository.save(reply);
        em.flush();
        em.clear();
        Reply findItem = replyRepository.findById(reply.getId()).orElseThrow();

        //then
        assertThat(reply.getId()).isEqualTo(findItem.getId());
        assertThat(reply.getNickname()).isEqualTo(findItem.getNickname());
    }

    @Test
    void 여러개_저장_및_조회() {
        //given
        Reply reply2 = DomainGenerator.createReply(post, member, "답글2");

        //when
        replyRepository.save(reply);
        replyRepository.save(reply2);

        long count = replyRepository.count();
        List<Reply> all = replyRepository.findAll();

        //then
        assertThat(count).isEqualTo(2);
        assertThat(all.size()).isEqualTo(2);
    }

    @Test
    void 답글을_저장하면_컨텐츠도_저장된다() {
        //when
        replyRepository.save(reply);
        em.flush();
        em.clear();
        Reply findItem = replyRepository.findById(reply.getId()).orElseThrow();

        //then
        assertThat(findItem.getContent()).isNotNull();
        assertThat(findItem.getContent().getId()).isNotNull();
    }

    @Test
    void ID가_일치하고_삭제상태가_아닌_답글을_조회한다() {
        //given
        replyRepository.save(reply);
        em.flush();
        em.clear();

        //when
        Optional<Reply> result = replyRepository.findByIdAndEnableIsTrue(
            this.reply.getId());

        //then
        Assertions.assertThat(result).isPresent();
    }

    @Test
    void ID가_일치하고_삭제상태가_아닌_답글을_조회할_때_삭제상태면_조회되지_않는다() {
        //given
        reply.delete();
        replyRepository.save(reply);
        em.flush();
        em.clear();

        //when
        Optional<Reply> result = replyRepository.findByIdAndEnableIsTrue(
            this.reply.getId());

        //then
        Assertions.assertThat(result).isEmpty();
    }

    @Test
    void 작성자가_일치하고_삭제상태가_아닌_답글을_조회하며_게시글을_페치조인한다() {
        //given
        replyRepository.save(reply);

        //when
        Page<Reply> result = replyRepository.findByWriterFetchJoin(member.getId(),
            PageRequest.of(0, 5));

        List<Reply> content = result.getContent();

        //then
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getTotalElements()).isEqualTo(1);

        for (Reply item : content) {
            assertThat(Hibernate.isInitialized(item.getPost())).isTrue();
        }
    }

    @Test
    void 게시글의_삭제되지_않은_답글_목록을_조회한다() {
        //given
        replyRepository.save(reply);
        em.flush();
        em.clear();

        //when
        List<Reply> result = replyRepository.findByPostAndEnableIsTrue(post);

        //then
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    void 게시글의_삭제되지_않은_답글_목록을_조회할_때_삭제상태인_답글은_조회되지_않는다() {
        //given
        reply.delete();
        replyRepository.save(reply);
        em.flush();
        em.clear();

        //when
        List<Reply> result = replyRepository.findByPostAndEnableIsTrue(post);

        //then
        Assertions.assertThat(result).isEmpty();
    }

    @Test
    void 삭제() {
        //given
        replyRepository.save(reply);

        //when
        Reply findItem = replyRepository.findById(reply.getId()).orElseThrow();
        replyRepository.delete(findItem);

        //then
        assertThat(replyRepository.count()).isEqualTo(0);
    }
}