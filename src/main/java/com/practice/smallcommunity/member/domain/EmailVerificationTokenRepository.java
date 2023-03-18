package com.practice.smallcommunity.member.domain;

import org.springframework.data.repository.CrudRepository;

public interface EmailVerificationTokenRepository extends CrudRepository<EmailVerificationToken, String>{

}
