package com.practice.smallcommunity.domain.post;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {

    Optional<Post> findByIdAndEnableIsTrue(Long id);

    /**
     * 삭제 상태가 아닌 게시글을 조회합니다.
     * @param id 게시글 ID
     * @return 게시글
     */
    @Query("select p from Post p join fetch p.mainText join fetch p.category where p.id = :id and p.enable is true")
    Optional<Post> findPostFetchJoin(@Param("id") Long id);

    /**
     * 회원이 작성한 게시글 목록을 반환합니다.
     *  삭제 상태인 게시글은 제외합니다.
     * @param writerId 회원( 작성자 ) ID
     * @param pageable 페이징 정보
     * @return 게시글 목록
     */
    @Query("select p from Post p where p.writer.id = :writerId and p.enable is true")
    Page<Post> findPostsByWriter(Long writerId, Pageable pageable);
}
