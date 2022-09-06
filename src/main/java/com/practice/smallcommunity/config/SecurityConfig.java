package com.practice.smallcommunity.config;

import com.practice.smallcommunity.security.JwtAuthenticationFilter;
import com.practice.smallcommunity.security.JwtProvider;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 보안 관련 구성 클래스입니다.
 */
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig implements WebMvcConfigurer {

    @Value("${spring.profiles.active}")
    private String profile;

    @Bean
    SecurityFilterChain web(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .httpBasic().disable()
            .formLogin().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .headers().frameOptions().sameOrigin()
            .and()
            .addFilterAfter(jwtAuthenticationFilter(), CorsFilter.class);

        configureRequestAuth(http);

        return http.build();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins("http://localhost:3000")
            .allowedMethods("*")
            .allowedHeaders("*")
            .allowCredentials(true);
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
    JwtProvider jwtTokenService() {
        return new JwtProvider();
    }

    private void configureRequestAuth(HttpSecurity http) throws Exception {
        if (profile.equals("dev")) {
            http.authorizeRequests()
                .antMatchers("/h2-console/**", "/actuator/**").permitAll();
        } else {
            http.authorizeRequests()
                .antMatchers("/actuator/**").hasRole("ADMIN");
        }

        http
            .authorizeRequests()
            // CORS
            .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            // 인증
            .antMatchers("/api/v1/auth/**").permitAll()
            // 회원가입
            .antMatchers(HttpMethod.POST, "/api/v1/members").anonymous()
            // 카테고리
            .antMatchers(HttpMethod.GET, "/api/v1/categories/**").permitAll()
            .antMatchers("/api/v1/categories/**").hasRole("ADMIN")
            // 게시글
            .antMatchers(HttpMethod.GET, "/api/v1/posts/**").permitAll()
            // 파일
            .antMatchers(HttpMethod.GET, "/api/v1/images/**").permitAll()
            .anyRequest().authenticated();
    }
}
