package com.practice.smallcommunity;

import com.practice.smallcommunity.auth.application.LoginService;
import com.practice.smallcommunity.category.CategoryService;
import com.practice.smallcommunity.inquiry.InquiryChatService;
import com.practice.smallcommunity.member.application.MemberService;
import com.practice.smallcommunity.post.application.PostService;
import com.practice.smallcommunity.post.application.dto.PostDto;
import com.practice.smallcommunity.reply.ReplyService;
import com.practice.smallcommunity.auth.domain.Login;
import com.practice.smallcommunity.category.Category;
import com.practice.smallcommunity.member.domain.Member;
import com.practice.smallcommunity.member.domain.MemberRole;
import com.practice.smallcommunity.post.domain.Post;
import com.practice.smallcommunity.reply.Reply;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Component
@Profile("dev")
public class TestData {

    private static final int MEMBER_COUNT = 10;
    private static final int POST_COUNT_PER_CATEGORIES = 10;
    private static final int REPLY_COUNT = 5;
    private static final int INQUIRY_CHAT_COUNT = 3;

    private final LoginService loginService;
    private final MemberService memberService;
    private final CategoryService categoryService;
    private final PostService postService;
    private final ReplyService replyService;
    private final InquiryChatService inquiryChatService;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        initMembers();
        initCategories();
        initPosts();
        initReplies();
        initInquiryChats();
    }

    private void initMembers() {
        Member admin = Member.builder()
            .email("admin@mail.com")
            .nickname("admin")
            .memberRole(MemberRole.ADMIN)
            .build();
        Login adminLogin = Login.builder()
            .member(admin)
            .password("adminPassword")
            .build();

        adminLogin.verifyEmail();
        loginService.register(adminLogin);

        for (int i = 0; i < MEMBER_COUNT; i++) {
            Member member = Member.builder()
                .email("user" + i + "@mail.com")
                .nickname("nickname" + i)
                .memberRole(MemberRole.USER)
                .build();
            Login login = Login.builder()
                .member(member)
                .password("user" + i + "password")
                .build();

            login.verifyEmail();
            loginService.register(login);
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
            .cudAdminOnly(true)
            .build();

        Category life = Category.builder()
            .code("free")
            .name("자유")
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
                String email = category.isCudAdminOnly() ? "admin@mail.com"
                    : "user" + i % MEMBER_COUNT + "@mail.com";
                Member member = memberService.findByEmail(email);
                PostDto dto = PostDto.builder()
                    .title(category.getName() + " " + i + "번 게시글")
                    .text(i + "번 게시글입니다.")
                    .build();

                postService.write(category, member, dto);
            }
        }
    }

    private void initReplies() {
        Post findPost = postService.findPost(1L);

        for (int i = 0; i < REPLY_COUNT; i++) {
            String userEmail = "user" + i % MEMBER_COUNT + "@mail.com";
            Member member = memberService.findByEmail(userEmail);

            Reply reply = Reply.builder()
                .post(findPost)
                .writer(member)
                .text(i + "번 답글입니다.")
                .build();

            replyService.add(reply);
        }
    }

    private void initInquiryChats() {
        Member admin = memberService.findByEmail("admin@mail.com");

        for (int i = 0; i < (MEMBER_COUNT / 2); i++) {
            String userEmail = "user" + i + "@mail.com";
            Member member = memberService.findByEmail(userEmail);
            for (int j = 0; j < INQUIRY_CHAT_COUNT; j++) {
                inquiryChatService.saveChat(member, member, "회원의" + j + "번째 채팅");
            }
            inquiryChatService.saveChat(member, admin, "관리자의 채팅");
        }
    }
}
