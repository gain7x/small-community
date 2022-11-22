package com.practice.smallcommunity.domain.auth.oauth2;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OAuth2LoginRepository extends JpaRepository<OAuth2Login, Long> {

    @EntityGraph(attributePaths = "member")
    Optional<OAuth2Login> findByUsernameAndPlatform(String username, OAuth2Platform platform);
}
