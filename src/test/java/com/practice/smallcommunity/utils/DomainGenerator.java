package com.practice.smallcommunity.utils;

import com.practice.smallcommunity.domain.attachment.UploadFile;
import com.practice.smallcommunity.domain.auth.oauth2.OAuth2Login;
import com.practice.smallcommunity.domain.auth.oauth2.OAuth2Platform;
import com.practice.smallcommunity.domain.category.Category;
import com.practice.smallcommunity.domain.content.Content;
import com.practice.smallcommunity.domain.content.VoteHistory;
import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.domain.member.MemberRole;
import com.practice.smallcommunity.domain.auth.Login;
import com.practice.smallcommunity.domain.notification.Notification;
import com.practice.smallcommunity.domain.notification.NotificationType;
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
            .nickname("nickname" + divider)
            .memberRole(MemberRole.USER)
            .build();
    }

    public static Login createLogin(Member member) {
        return Login.builder()
            .member(member)
            .password("testPassword")
            .build();
    }

    public static OAuth2Login createOAuth2Login(Member member, String username, OAuth2Platform platform) {
        return OAuth2Login.builder()
            .member(member)
            .username(username)
            .platform(platform)
            .build();
    }

    public static Post createPost(Category category, Member member, String divider) {
        return Post.builder()
            .writer(member)
            .category(category)
            .title("title " + divider)
            .text("text " + divider)
            .build();
    }

    public static Reply createReply(Post post, Member writer, String text) {
        return Reply.builder()
            .post(post)
            .writer(writer)
            .text(text)
            .build();
    }

    public static Notification createNotification(Member receiver, Post relatedPost) {
        return Notification.builder()
            .receiver(receiver)
            .sender("TEST")
            .relatedPost(relatedPost)
            .type(NotificationType.SYSTEM)
            .build();
    }

    public static UploadFile createUploadFile(Member uploader, String divider) {
        return UploadFile.builder()
            .uploader(uploader)
            .bucket("Bucket" + divider)
            .filename("Original" + divider)
            .url("http://localhost/Bucket" + divider + "/Original" + divider)
            .build();
    }
}
