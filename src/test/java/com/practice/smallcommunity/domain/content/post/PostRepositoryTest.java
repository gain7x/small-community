package com.practice.smallcommunity.domain.content.post;

import static org.assertj.core.api.Assertions.assertThat;

import com.practice.smallcommunity.domain.content.Content;
import com.practice.smallcommunity.domain.content.ContentRepository;
import java.util.List;
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
class PostRepositoryTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    ContentRepository contentRepository;

    @Autowired
    PostRepository postRepository;

    Content content = Content.builder()
        .text("컨텐츠")
        .build();

    Post post = Post.builder()
        .title("제목")
        .content(content)
        .build();

    @BeforeEach
    void beforeEach() {
        contentRepository.save(content);
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
        Content content2 = Content.builder()
            .text("컨텐츠2")
            .build();
        Post post2 = Post.builder()
            .title("제목2")
            .content(content2)
            .build();

        //when
        postRepository.save(post);

        contentRepository.save(content2);
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