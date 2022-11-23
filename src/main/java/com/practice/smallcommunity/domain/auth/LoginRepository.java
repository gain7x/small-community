package com.practice.smallcommunity.domain.auth;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LoginRepository extends JpaRepository<Login, Long> {

    @Query("select l from Login l where l.member.id = :memberId")
    @EntityGraph(attributePaths = "member")
    Optional<Login> findByMemberIdFetchJoin(@Param("memberId") Long memberId);
}
