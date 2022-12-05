package com.practice.smallcommunity.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.smallcommunity.security.CookieOAuth2AuthorizationRequestRepository;
import com.practice.smallcommunity.security.JwtAuthenticationFilter;
import com.practice.smallcommunity.security.JwtProvider;
import com.practice.smallcommunity.security.OAuth2AuthenticationFailureHandler;
import com.practice.smallcommunity.security.OAuth2AuthenticationSuccessHandler;
import com.practice.smallcommunity.security.RestAccessDeniedHandler;
import com.practice.smallcommunity.security.RestAuthenticationEntryPoint;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
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

    @Value("${verification.email.api}")
    private String emailVerificationApi;

    @Value("${oauth2.authorizedDomains}")
    private String[] authorizedDomains;

    private final JwtProvider jwtProvider;
    private final OAuth2AuthenticationSuccessHandler oauth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oauth2AuthenticationFailureHandler;
    private final MessageSource ms;
    private final ObjectMapper objectMapper;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping(emailVerificationApi)
            .allowedOrigins("*")
            .allowedMethods("POST");

        registry.addMapping("/**")
            .allowedOrigins(authorizedDomains)
            .allowedMethods("*")
            .allowedHeaders("*")
            .allowCredentials(true);
    }

    @Bean
    WebSecurityCustomizer ignoringCustomizer() {
        return (web) -> web.ignoring()
            .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Bean
    SecurityFilterChain web(HttpSecurity http) throws Exception {
        http
            .cors()
            .and()
            .csrf().disable()
            .httpBasic().disable()
            .formLogin().disable()
            .headers().frameOptions().sameOrigin()
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http
            .oauth2Login()
            .successHandler(oauth2AuthenticationSuccessHandler)
            .failureHandler(oauth2AuthenticationFailureHandler)
            .authorizationEndpoint()
            .authorizationRequestRepository(new CookieOAuth2AuthorizationRequestRepository(List.of(authorizedDomains)));

        http
            .addFilterAfter(jwtAuthenticationFilter(), CorsFilter.class)
            .exceptionHandling()
            .authenticationEntryPoint(new RestAuthenticationEntryPoint(ms, objectMapper))
            .accessDeniedHandler(new RestAccessDeniedHandler(ms, objectMapper));

        configureRequestAuth(http);

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
        return new JwtAuthenticationFilter(jwtProvider);
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
            .antMatchers(HttpMethod.POST, "/api/v1/oauth2").anonymous()
            .antMatchers(HttpMethod.POST, "/api/v1/members").anonymous()
            .antMatchers(HttpMethod.POST, emailVerificationApi).anonymous()
            // 카테고리
            .antMatchers(HttpMethod.GET, "/api/v1/categories/**").permitAll()
            .antMatchers("/api/v1/categories/**").hasRole("ADMIN")
            // 게시글
            .antMatchers(HttpMethod.GET, "/api/v1/posts/**").permitAll()
            // 답글
            .antMatchers(HttpMethod.GET, "/api/v1/replies/**").permitAll()
            // 파일
            .antMatchers(HttpMethod.GET, "/api/v1/images/**").permitAll()
            .anyRequest().authenticated();
    }
}
