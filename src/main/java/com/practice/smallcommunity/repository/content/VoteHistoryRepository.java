package com.practice.smallcommunity.repository.content;

import com.practice.smallcommunity.domain.content.VoteHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteHistoryRepository extends JpaRepository<VoteHistory, Long> {

}
