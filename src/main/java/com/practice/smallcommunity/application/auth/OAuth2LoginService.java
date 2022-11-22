package com.practice.smallcommunity.application.auth;

import com.practice.smallcommunity.application.exception.BusinessException;
import com.practice.smallcommunity.application.exception.ErrorCode;
import com.practice.smallcommunity.domain.auth.oauth2.OAuth2Login;
import com.practice.smallcommunity.domain.auth.oauth2.OAuth2LoginRepository;
import com.practice.smallcommunity.domain.auth.oauth2.OAuth2Platform;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class OAuth2LoginService {

    private final OAuth2LoginRepository oAuth2LoginRepository;

    /**
     * OAuth2 사용자 ID와 플랫폼이 일치하는 OAuth2 로그인 정보를 조회합니다.
     * @param username OAuth2 사용자 ID
     * @param platform 플랫폼
     * @return OAuth2 로그인 정보
     * @throws BusinessException
     *          일치하는 정보가 없는 경우
     */
    public OAuth2Login findOne(String username, OAuth2Platform platform) {
        return oAuth2LoginRepository.findByUsernameAndPlatform(username, platform)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_OAUTH2_LOGIN));
    }

    /**
     * OAuth2 로그인/회원 정보를 등록하고, 등록된 정보를 반환합니다.
     * @param oAuth2Login OAuth2 로그인/회원 정보
     */
    public OAuth2Login register(OAuth2Login oAuth2Login) {
        return oAuth2LoginRepository.save(oAuth2Login);
    }
}
