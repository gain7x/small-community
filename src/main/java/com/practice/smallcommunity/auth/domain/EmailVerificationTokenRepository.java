package com.practice.smallcommunity.auth.domain;

import org.springframework.data.repository.CrudRepository;

public interface EmailVerificationTokenRepository extends CrudRepository<EmailVerificationToken, String>{

}
