package com.practice.smallcommunity.domain.post;

import static org.assertj.core.api.Assertions.assertThat;

import com.practice.smallcommunity.domain.board.Board;
import com.practice.smallcommunity.domain.board.BoardRepository;
import com.practice.smallcommunity.domain.category.Category;
import com.practice.smallcommunity.domain.category.CategoryRepository;
import com.practice.smallcommunity.domain.content.Content;
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
    BoardRepository boardRepository;

    @Autowired
    ContentRepository contentRepository;

    @Autowired
    PostRepository postRepository;

    Member member = DomainGenerator.createMember("A");
    Category category = DomainGenerator.createCategory("개발");
    Board board = DomainGenerator.createBoard(category, "Java");
    Content content = DomainGenerator.createContent(member, "A");
    Post post = DomainGenerator.createPost(board, member, content);

    @BeforeEach
    void beforeEach() {
        categoryRepository.save(category);
        boardRepository.save(board);
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
        Content content2 = DomainGenerator.createContent(member, "B");
        Post post2 = DomainGenerator.createPost(board, member, content2);

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

        //then
        assertThat(content.getId()).isNotNull();
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