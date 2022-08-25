package com.practice.smallcommunity.domain.content;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteHistoryRepository extends JpaRepository<VoteHistory, VoteHistoryId> {

}
