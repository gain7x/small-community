package com.practice.smallcommunity.domain.post;

import static org.assertj.core.api.Assertions.assertThat;

import com.practice.smallcommunity.domain.board.Board;
import com.practice.smallcommunity.domain.board.BoardRepository;
import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.domain.member.MemberRepository;
import com.practice.smallcommunity.domain.member.MemberRole;
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
    BoardRepository boardRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PostRepository postRepository;

    Board board = Board.builder()
        .name("개발")
        .code("DEV")
        .enable(true)
        .build();

    Member member = Member.builder()
        .email("userA@mail.com")
        .password("password")
        .nickname("nickname")
        .memberRole(MemberRole.ROLE_USER)
        .build();

    Post post = Post.builder()
        .board(board)
        .writer(member)
        .title("제목")
        .content("내용")
        .build();

    @BeforeEach
    void beforeEach() {
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
        Post post2 = Post.builder()
            .board(board)
            .writer(member)
            .title("제목2")
            .content("내용2")
            .build();

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