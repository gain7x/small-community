package com.practice.smallcommunity.auth.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LoginRepository extends JpaRepository<Login, Long> {

    @Query("select l from Login l join fetch l.member where l.member.id = :memberId")
    Optional<Login> findByMemberIdFetchJoin(@Param("memberId") Long memberId);
}
