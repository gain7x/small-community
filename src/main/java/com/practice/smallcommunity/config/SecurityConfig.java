package com.practice.smallcommunity.config;

import com.practice.smallcommunity.security.JwtAuthenticationFilter;
import com.practice.smallcommunity.security.JwtTokenProvider;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.CorsFilter;

/**
 * 보안 관련 구성 클래스입니다.
 */
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain web(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .httpBasic().disable()
            .formLogin().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .addFilterAfter(jwtAuthenticationFilter(), CorsFilter.class)
            .authorizeRequests()
            // 인증
            .antMatchers("/api/v1/auth").permitAll()
            // 회원
            .antMatchers(HttpMethod.POST, "/api/v1/members").anonymous()
            .antMatchers("/api/v1/members/**").authenticated()
            // 카테고리
            .antMatchers(HttpMethod.GET, "/api/v1/categories/**").permitAll()
            .antMatchers("/api/v1/categories/**").hasRole("ADMIN")
            // 게시글
            .antMatchers(HttpMethod.GET, "/api/v1/posts/**").permitAll()
            .antMatchers("/api/v1/posts/**").authenticated()
            // 답글
            .antMatchers("/api/v1/replies/**").authenticated()
            .anyRequest().authenticated();

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        String idForEncode = "bcrypt";

        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put(idForEncode, new BCryptPasswordEncoder());

        return new DelegatingPasswordEncoder(idForEncode, encoders);
    }

    @Bean
    JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenService());
    }

    @Bean
    JwtTokenProvider jwtTokenService() {
        return new JwtTokenProvider();
    }
}
