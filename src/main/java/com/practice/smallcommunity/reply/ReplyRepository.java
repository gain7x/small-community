package com.practice.smallcommunity.reply;

import com.practice.smallcommunity.post.domain.Post;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

    Optional<Reply> findByIdAndEnableIsTrue(Long id);

    List<Reply> findByPostAndEnableIsTrue(Post post);

    /**
     * 회원이 작성한 답글 목록을 조회합니다.
     *  삭제 상태인 답글은 제외합니다.
     *  게시글을 페치조인합니다.
     * @param writerId 회원( 작성자 ) ID
     * @param pageable 페이징 정보
     * @return 답글 목록
     */
    @Query(value = "select r from Reply r join fetch r.post where r.writer.id = :writerId and r.enable is true",
        countQuery = "select count(r) from Reply r where r.writer.id = :writerId and r.enable is true")
    Page<Reply> findByWriterFetchJoin(@Param("writerId") Long writerId, Pageable pageable);
}
