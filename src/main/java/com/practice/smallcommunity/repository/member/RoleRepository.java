package com.practice.smallcommunity.repository.member;

import com.practice.smallcommunity.domain.member.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

}
