package com.practice.smallcommunity.auth.domain;

import java.util.Optional;

import com.practice.smallcommunity.member.domain.OAuth2Platform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OAuth2LoginRepository extends JpaRepository<OAuth2Login, Long> {

    @Query("select o from OAuth2Login o join fetch o.member where o.username = :username and o.platform = :platform")
    Optional<OAuth2Login> findOneFetchJoin(@Param("username") String username, @Param("platform") OAuth2Platform platform);
}
