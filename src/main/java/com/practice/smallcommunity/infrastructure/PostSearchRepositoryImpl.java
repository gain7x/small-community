package com.practice.smallcommunity.infrastructure;

import static com.practice.smallcommunity.post.domain.QPost.post;

import com.practice.smallcommunity.post.domain.Post;
import com.practice.smallcommunity.post.domain.PostSearchRepository;
import com.practice.smallcommunity.post.domain.dto.BoardSearchCond;
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
public class PostSearchRepositoryImpl implements PostSearchRepository {

    private final JPAQueryFactory queryFactory;

    public PostSearchRepositoryImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public Page<Post> searchPosts(BoardSearchCond cond, Pageable pageable) {
        QueryResults<Post> results = queryFactory.selectFrom(post)
            .where(postEnabled(), categoryEq(cond.getCategoryId()), titleMatch(cond.getTitle()))
            .orderBy(sortPost())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }

    private BooleanExpression categoryEq(Long categoryId) {
        return categoryId == null ? null : post.category.id.eq(categoryId);
    }

    private BooleanExpression postEnabled() {
        return post.enable.eq(true);
    }

    private BooleanExpression titleMatch(String title) {
        return StringUtils.hasText(title) ? match(post.title, "+" + title + "*") : null;
    }

    private BooleanExpression match(StringPath field, String expression) {
        return Expressions.numberTemplate(Double.class,
                "function('match', {0}, {1})", field, expression)
            .gt(0);
    }

    private OrderSpecifier<?> sortPost() {
        return new OrderSpecifier<>(Order.DESC, post.createdDate);
    }
}
