package com.practice.smallcommunity.domain.post;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

    Optional<Post> findByIdAndEnableIsTrue(Long id);
}
