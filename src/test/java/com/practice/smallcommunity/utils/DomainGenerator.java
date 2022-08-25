package com.practice.smallcommunity.utils;

import com.practice.smallcommunity.domain.board.Board;
import com.practice.smallcommunity.domain.category.Category;
import com.practice.smallcommunity.domain.content.Content;
import com.practice.smallcommunity.domain.content.VoteHistory;
import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.domain.member.MemberRole;
import com.practice.smallcommunity.domain.post.Post;

public abstract class DomainGenerator {

    public static Board createBoard(Category category, String name) {
        return Board.builder()
            .category(category)
            .name(name)
            .enable(true)
            .build();
    }

    public static Category createCategory(String name) {
        return Category.builder()
            .name(name)
            .enable(true)
            .build();
    }

    public static Content createContent(Member member, String divider) {
        return Content.builder()
            .writer(member)
            .text("contents" + divider)
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
            .memberRole(MemberRole.ROLE_USER)
            .build();
    }

    public static Post createPost(Board board, Member member) {
        return Post.builder()
            .writer(member)
            .board(board)
            .title("title")
            .content("content")
            .build();
    }
}
