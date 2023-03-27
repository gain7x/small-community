package com.practice.smallcommunity.inquiry.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface InquiryChatRepository extends JpaRepository<InquiryChat, Long> {

    @Query("select ic from InquiryChat ic where ic.inquirer.id = :inquirerId order by ic.createdDate")
    Page<InquiryChat> searchInquiryChats(Long inquirerId, Pageable pageable);
}
