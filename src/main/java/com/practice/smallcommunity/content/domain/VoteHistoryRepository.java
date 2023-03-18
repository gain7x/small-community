package com.practice.smallcommunity.content.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteHistoryRepository extends JpaRepository<VoteHistory, VoteHistoryId> {

}
