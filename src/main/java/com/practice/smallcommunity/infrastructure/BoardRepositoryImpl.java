package com.practice.smallcommunity.infrastructure;

import static com.practice.smallcommunity.domain.content.QContent.content;
import static com.practice.smallcommunity.domain.post.QPost.post;

import com.practice.smallcommunity.domain.post.BoardRepository;
import com.practice.smallcommunity.domain.post.Post;
import com.practice.smallcommunity.domain.post.dto.BoardSearchCond;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import javax.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
public class BoardRepositoryImpl implements BoardRepository {

    private final JPAQueryFactory queryFactory;

    public BoardRepositoryImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public Page<Post> searchPosts(BoardSearchCond cond, Pageable pageable) {
        QueryResults<Post> results = queryFactory.selectFrom(post)
            .join(post.content, content)
            .on(categoryEq(cond.getCategoryId()))
            .where(titleMatch(cond.getTitle()))
            .orderBy(sortPost(pageable))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }

    private BooleanExpression categoryEq(Long categoryId) {
        return categoryId != null ? post.category.id.eq(categoryId) : null;
    }

    private BooleanExpression titleMatch(String title) {
        return StringUtils.hasText(title) ? match(post.title, "+" + title + "*") : null;
    }

    private BooleanExpression match(StringPath field, String expression) {
        return Expressions.numberTemplate(Double.class,
                "function('match', {0}, {1})", field, expression)
            .gt(0);
    }

    private OrderSpecifier<?> sortPost(Pageable pageable) {
        return new OrderSpecifier<>(Order.DESC, post.createdDate);
    }
}
