package com.practice.smallcommunity.domain.reply;

import com.practice.smallcommunity.domain.post.Post;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

    Optional<Reply> findByIdAndEnableIsTrue(Long id);

    List<Reply> findByPostAndEnableIsTrue(Post post);
}
