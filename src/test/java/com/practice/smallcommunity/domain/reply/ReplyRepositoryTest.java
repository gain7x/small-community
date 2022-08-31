package com.practice.smallcommunity.domain.reply;

import static org.assertj.core.api.Assertions.*;

import com.practice.smallcommunity.domain.category.Category;
import com.practice.smallcommunity.domain.category.CategoryRepository;
import com.practice.smallcommunity.domain.content.ContentRepository;
import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.domain.member.MemberRepository;
import com.practice.smallcommunity.domain.post.Post;
import com.practice.smallcommunity.domain.post.PostRepository;
import com.practice.smallcommunity.utils.DomainGenerator;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

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
        assertThat(result).isPresent();
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
        assertThat(result).isEmpty();
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
        assertThat(result).isEmpty();
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