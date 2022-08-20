package com.practice.smallcommunity.repository.member;

import com.practice.smallcommunity.domain.member.MemberRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRoleRepository extends JpaRepository<MemberRole, Long> {

}
