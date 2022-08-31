package com.practice.smallcommunity.utils;

import com.practice.smallcommunity.domain.category.Category;
import com.practice.smallcommunity.domain.content.Content;
import com.practice.smallcommunity.domain.content.VoteHistory;
import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.domain.member.MemberRole;
import com.practice.smallcommunity.domain.post.Post;
import com.practice.smallcommunity.domain.reply.Reply;

public abstract class DomainGenerator {

    public static Category createCategory(String code, String name) {
        return Category.builder()
            .code(code)
            .name(name)
            .enable(true)
            .build();
    }

    public static Content createContent(Member member) {
        return Content.builder()
            .member(member)
            .build();
    }

    public static VoteHistory createVoteHistory(Member voter, Content content, boolean positive) {
        return VoteHistory.builder()
            .voter(voter)
            .content(content)
            .positive(positive)
            .build();
    }

    public static Member createMember(String divider) {
        return Member.builder()
            .email("user" + divider + "@mail.com")
            .password("password" + divider)
            .nickname("nickname" + divider)
            .memberRole(MemberRole.USER)
            .build();
    }

    public static Post createPost(Category category, Member member, String text) {
        return Post.builder()
            .writer(member)
            .category(category)
            .title("title")
            .text(text)
            .build();
    }

    public static Reply createReply(Post post, Member writer, String text) {
        return Reply.builder()
            .post(post)
            .writer(writer)
            .text(text)
            .build();
    }
}
