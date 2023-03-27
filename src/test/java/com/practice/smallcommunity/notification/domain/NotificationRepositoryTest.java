package com.practice.smallcommunity.notification.domain;

import static org.assertj.core.api.Assertions.*;

import com.practice.smallcommunity.category.Category;
import com.practice.smallcommunity.category.CategoryRepository;
import com.practice.smallcommunity.member.domain.Member;
import com.practice.smallcommunity.member.domain.MemberRepository;
import com.practice.smallcommunity.post.domain.Post;
import com.practice.smallcommunity.post.domain.PostRepository;
import com.practice.smallcommunity.testutils.DomainGenerator;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@DataJpaTest
class NotificationRepositoryTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    NotificationRepository notificationRepository;

    Member receiver = DomainGenerator.createMember("A");
    Category category = DomainGenerator.createCategory("test", "테스트");
    Post post = DomainGenerator.createPost(category, receiver, "게시글");
    Notification notification = DomainGenerator.createNotification(receiver, post);

    @BeforeEach
    void setUp() {
        memberRepository.save(receiver);
        categoryRepository.save(category);
        postRepository.save(post);
    }

    @Test
    void 저장_및_조회() {
        //when
        notificationRepository.save(notification);
        em.flush();
        em.clear();
        Notification findItem = notificationRepository.findById(notification.getId())
            .orElseThrow();

        //then
        assertThat(notification.getId()).isEqualTo(findItem.getId());
        assertThat(notification.getReceiver().getId()).isEqualTo(findItem.getReceiver().getId());
    }

    @Test
    void 여러개_저장_및_조회() {
        //given
        Notification notification2 = DomainGenerator.createNotification(receiver, post);

        //when
        notificationRepository.save(notification);
        notificationRepository.save(notification2);

        long count = notificationRepository.count();
        List<Notification> all = notificationRepository.findAll();

        //then
        assertThat(count).isEqualTo(2);
        assertThat(all.size()).isEqualTo(2);
    }

    @Test
    void 유효한_알림_목록을_조회한다() {
        //given
        notificationRepository.save(notification);

        //when
        Page<Notification> result = notificationRepository.findRecentNotifications(
            receiver.getId(), PageRequest.of(0, 5));

        //then
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void 읽지_않은_알림을_모두_읽음_처리한다() {
        //given
        notificationRepository.save(notification);
        em.flush();
        em.clear();

        //when
        notificationRepository.readAllUnreadNotifications(receiver.getId());
        Notification result = notificationRepository.findAll().get(0);

        //then
        assertThat(result.isRead()).isEqualTo(true);
        assertThat(result.getLastModifiedDate()).isNotEqualTo(result.getCreatedDate());
    }

    @Test
    void 삭제() {
        //given
        notificationRepository.save(notification);

        //when
        Notification findItem = notificationRepository.findById(notification.getId())
            .orElseThrow();
        notificationRepository.delete(findItem);

        //then
        assertThat(notificationRepository.count()).isEqualTo(0);
    }

    @EnableJpaAuditing
    @TestConfiguration
    static class TestConfig {

    }
}