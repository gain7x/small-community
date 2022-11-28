package com.practice.smallcommunity.domain.auth;

import org.springframework.data.repository.CrudRepository;

public interface EmailVerificationTokenRepository extends CrudRepository<EmailVerificationToken, String>{

}
