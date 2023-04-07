package com.practice.smallcommunity.inquiry.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface InquiryChatRepository extends JpaRepository<InquiryChat, Long> {

    /**
     * 사용자의 문의 채팅을 최근 것부터 조회합니다.
     */
    @Query("select ic from InquiryChat ic where ic.inquirer.id = :inquirerId order by ic.createdDate desc")
    Page<InquiryChat> findChatsByInquirerId(Long inquirerId, Pageable pageable);

    /**
     * 각 사용자별 최근 문의 채팅을 조회하며 문의 회원을 페치조인합니다.
     */
    @Query(value = "select ic from InquiryChat ic join fetch ic.inquirer where ic.id in (select max(id) from InquiryChat group by inquirer.id) order by ic.createdDate desc",
    countQuery = "select count(*) from InquiryChat group by inquirer.id")
    Page<InquiryChat> findEachInquirerLatestChatFetchJoin(Pageable pageable);
}