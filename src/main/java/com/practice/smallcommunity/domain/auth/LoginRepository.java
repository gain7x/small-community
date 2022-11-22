package com.practice.smallcommunity.domain.auth;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginRepository extends JpaRepository<Login, Long> {

    @EntityGraph(attributePaths = "member")
    Optional<Login> findByMemberId(Long memberId);
}
