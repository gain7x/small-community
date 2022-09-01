package com.practice.smallcommunity.domain.post;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {

    Optional<Post> findByIdAndEnableIsTrue(Long id);

    /**
     * 삭제 상태가 아닌 게시글을 조회하며 본문까지 페치조인으로 가져옵니다.
     * @param id 게시글 ID
     * @return 게시글
     */
    @Query("select p from Post p where p.id = :id")
    @EntityGraph(attributePaths = {"mainText"})
    Optional<Post> findPostWithMainText(@Param("id") Long id);
}
