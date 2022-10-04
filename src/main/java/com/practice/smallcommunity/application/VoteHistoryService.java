package com.practice.smallcommunity.application;

import com.practice.smallcommunity.domain.content.Content;
import com.practice.smallcommunity.domain.content.VoteHistory;
import com.practice.smallcommunity.domain.content.VoteHistoryId;
import com.practice.smallcommunity.domain.content.VoteHistoryRepository;
import com.practice.smallcommunity.domain.member.Member;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class VoteHistoryService {

    private final VoteHistoryRepository voteHistoryRepository;

    public Optional<VoteHistory> findVoteHistory(Member voter, Content content) {
        return voteHistoryRepository.findById(new VoteHistoryId(voter.getId(), content.getId()));
    }

    /**
     * 컨텐츠 투표 기록을 추가합니다.
     *  이전 투표 기록이 없는 경우에만 추가됩니다.
     *  이전 투표 기록이 존재하고, 현재 추가하려는 투표 기록과 긍정 여부가 다른 경우 이전 투표 기록을 삭제합니다.
     * @param voter 투표 회원
     * @param content 투표 컨텐츠
     * @param positive 긍정 여부
     * @return 추가/삭제가 발생한 경우 TRUE,
     *          이전 투표 기록과 동일하여 아무 작업도 발생하지 않은 경우 FALSE
     */
    public boolean addVoteHistory(Member voter, Content content, boolean positive) {
        VoteHistory newVoteHistory = VoteHistory.builder()
            .voter(voter)
            .content(content)
            .positive(positive)
            .build();

        Optional<VoteHistory> prevVoteHistoryOptional = voteHistoryRepository.findById(
            newVoteHistory.getId());
        if (prevVoteHistoryOptional.isEmpty()) {
            voteHistoryRepository.save(newVoteHistory);
            return true;
        }

        VoteHistory prevVoteHistory = prevVoteHistoryOptional.get();
        if (prevVoteHistory.isPositive() != newVoteHistory.isPositive()) {
            voteHistoryRepository.delete(prevVoteHistory);
            return true;
        }

        return false;
    }
}
