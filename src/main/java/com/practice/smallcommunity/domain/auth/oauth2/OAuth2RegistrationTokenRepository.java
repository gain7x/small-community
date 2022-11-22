package com.practice.smallcommunity.domain.auth.oauth2;

import org.springframework.data.repository.CrudRepository;

public interface OAuth2RegistrationTokenRepository extends CrudRepository<OAuth2RegistrationToken, String> {

}
