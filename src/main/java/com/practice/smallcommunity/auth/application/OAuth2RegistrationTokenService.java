package com.practice.smallcommunity.auth.application;

import com.practice.smallcommunity.common.exception.BusinessException;
import com.practice.smallcommunity.common.exception.ErrorCode;
import com.practice.smallcommunity.auth.domain.oauth2.OAuth2Platform;
import com.practice.smallcommunity.auth.domain.oauth2.OAuth2RegistrationToken;
import com.practice.smallcommunity.auth.domain.oauth2.OAuth2RegistrationTokenRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OAuth2RegistrationTokenService {

    @Value("${verification.oauth2.expirationMinutes}")
    private long registerTokenExpirationMinutes;
    private final OAuth2RegistrationTokenRepository oauth2RegistrationTokenRepository;

    /**
     * 소셜 회원의 가입용 토큰을 저장하고, 접근할 수 있는 키를 반환합니다.
     * @param email 이메일
     * @param username OAuth2 사용자 ID
     * @param platform OAuth2 플랫폼
     * @return 키
     */
    public String createRegistrationToken(String email, String username, OAuth2Platform platform) {
        String key = UUID.randomUUID().toString();
        oauth2RegistrationTokenRepository.save(OAuth2RegistrationToken.builder()
            .email(email)
            .key(key)
            .username(username)
            .platform(platform)
            .expirationMinutes(registerTokenExpirationMinutes)
            .build());

        return key;
    }

    /**
     * 이메일과 키가 일치하는 가입용 토큰을 반환합니다.
     * @param key 토큰 키
     * @return 가입용 토큰
     * @throws BusinessException
     *          이메일과 키가 일치하는 가입용 토큰이 없는 경우
     */
    public OAuth2RegistrationToken findOne(String email, String key) {
        Optional<OAuth2RegistrationToken> token = oauth2RegistrationTokenRepository.findById(email);
        if (token.isEmpty() || !token.get().getKey().equals(key)) {
            throw new BusinessException(ErrorCode.INVALID_OAUTH2_REGISTRATION_KEY);
        }
        return token.get();
    }
}
