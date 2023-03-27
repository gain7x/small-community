package com.practice.smallcommunity.security;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import static com.practice.smallcommunity.utils.SecurityUtil.extractBearerToken;

/**
 * JWT 토큰 검증 후 인증 객체를 저장하는 필터입니다.
 */
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    /**
     * 클라이언트 요청에 토큰이 있으면 검증을 진행하고, 성공 시 인증 객체를 보안 컨텍스트에 저장합니다.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        SecurityContext context = SecurityContextHolder.getContext();
        if (context.getAuthentication() == null) {
            String jwt = getBearerToken(request);
            if (StringUtils.hasText(jwt)) {
                Authentication authentication = jwtProvider.authenticate(jwt);
                context.setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 클라이언트 요청에 Bearer 토큰이 있으면 토큰을 반환합니다.
     * @param request 클라이언트 요청
     * @return 토큰이 존재하면 'Bearer ' 문자열을 제외한 토큰 문자열, 아니면 null
     */
    private String getBearerToken(HttpServletRequest request) {
        return extractBearerToken(request.getHeader("Authorization"));
    }
}
