package com.practice.smallcommunity;

import com.practice.smallcommunity.application.CategoryService;
import com.practice.smallcommunity.application.MemberService;
import com.practice.smallcommunity.application.PostService;
import com.practice.smallcommunity.application.ReplyService;
import com.practice.smallcommunity.application.dto.PostDto;
import com.practice.smallcommunity.domain.category.Category;
import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.domain.member.MemberRole;
import java.util.List;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
@Transactional
@Profile("dev")
public class TestData {

    private static final int MEMBER_COUNT = 30;
    private static final int POST_COUNT_PER_CATEGORIES = 10;

    private final MemberService memberService;
    private final CategoryService categoryService;
    private final PostService postService;
    private final ReplyService replyService;

    @PostConstruct
    void init() {
        initMembers();
        initCategories();
        initPosts();
        initReplies();
    }

    private void initMembers() {
        Member admin = Member.builder()
            .email("admin@mail.com")
            .password("adminPassword")
            .nickname("admin")
            .memberRole(MemberRole.ADMIN)
            .build();

        memberService.register(admin);

        for (int i = 0; i < MEMBER_COUNT; i++) {
            Member member = Member.builder()
                .email("user" + i + "@mail.com")
                .password("user" + i + "password")
                .nickname("user" + i)
                .memberRole(MemberRole.USER)
                .build();

            memberService.register(member);
        }
    }

    private void initCategories() {
        Category dev = Category.builder()
            .code("tech")
            .name("기술")
            .enable(true)
            .build();

        Category qna = Category.builder()
            .code("qna")
            .name("Q&A")
            .enable(true)
            .build();

        Category notice = Category.builder()
            .code("notice")
            .name("공지사항")
            .enable(true)
            .build();

        Category life = Category.builder()
            .code("life")
            .name("일상")
            .enable(true)
            .build();

        categoryService.register(dev);
        categoryService.register(qna);
        categoryService.register(notice);
        categoryService.register(life);
    }

    private void initPosts() {
        List<Category> categories = categoryService.findEnableCategories();
        for (Category category : categories) {
            for (int i = 0; i < POST_COUNT_PER_CATEGORIES; i++) {
                String userEmail = "user" + i % MEMBER_COUNT + "@mail.com";
                Member member = memberService.findByEmail(userEmail);
                PostDto dto = PostDto.builder()
                    .title(i + "번 게시글")
                    .text(i + "번 게시글입니다.")
                    .build();

                postService.write(category, member, dto);
            }
        }
    }

    private void initReplies() {

    }
}
