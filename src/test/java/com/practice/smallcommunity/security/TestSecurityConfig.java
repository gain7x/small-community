package com.practice.smallcommunity.security;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestSecurityConfig {

    @Bean
    public JwtProvider jwtProvider() {
        return new JwtProvider();
    }

    @Bean
    public OAuth2AuthenticationSuccessHandler authenticationSuccessHandler() {
        return Mockito.mock(OAuth2AuthenticationSuccessHandler.class);
    }

    @Bean
    public OAuth2AuthenticationFailureHandler authenticationFailureHandler() {
        return Mockito.mock(OAuth2AuthenticationFailureHandler.class);
    }
}