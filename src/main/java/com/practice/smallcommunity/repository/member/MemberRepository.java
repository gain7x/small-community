package com.practice.smallcommunity.repository.member;

import com.practice.smallcommunity.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

}
