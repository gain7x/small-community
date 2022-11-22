package com.practice.smallcommunity.application.auth;

import com.practice.smallcommunity.application.exception.BusinessException;
import com.practice.smallcommunity.application.exception.ErrorCode;
import com.practice.smallcommunity.domain.auth.oauth2.OAuth2Platform;
import com.practice.smallcommunity.domain.auth.oauth2.OAuth2RegistrationToken;
import com.practice.smallcommunity.domain.auth.oauth2.OAuth2RegistrationTokenRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OAuth2RegistrationTokenService {

    @Value("${verification.oauth2.expirationMinutes}")
    private long registerTokenExpirationMinutes;
    private final OAuth2RegistrationTokenRepository oAuth2RegistrationTokenRepository;

    /**
     * 소셜 회원의 가입용 토큰을 저장하고, 접근할 수 있는 키를 반환합니다.
     * @param email 이메일
     * @param username OAuth2 사용자 ID
     * @param platform OAuth2 플랫폼
     * @return 키
     */
    public String createRegistrationToken(String email, String username, OAuth2Platform platform) {
        String key = UUID.randomUUID().toString();
        oAuth2RegistrationTokenRepository.save(OAuth2RegistrationToken.builder()
            .key(key)
            .email(email)
            .username(username)
            .platform(platform)
            .expirationMinutes(registerTokenExpirationMinutes)
            .build());

        return key;
    }

    /**
     * 키가 일치하는 가입용 토큰을 반환합니다.
     * @param key 토큰 키
     * @return 가입용 토큰
     * @throws BusinessException
     *          키가 일치하는 가입용 토큰이 없는 경우
     */
    public OAuth2RegistrationToken findByKey(String key) {
        return oAuth2RegistrationTokenRepository.findById(key)
            .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_OAUTH2_REGISTRATION_KEY));
    }
}
