package com.practice.smallcommunity.security;

import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.security.dto.TokenDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

/**
 * JWT 발급 및 검증 서비스입니다.
 */
@RequiredArgsConstructor
@Component
public class JwtProvider {

    private static final String ROLE_CLAIM = "AUTH";

    private final JwtProperties properties;

    /**
     * 회원 정보를 기반으로 액세스 토큰을 생성하여 반환합니다.
     * @param member 회원
     * @return 토큰 정보
     */
    public TokenDto createAccessToken(Member member) {
        Date expires = Date.from(
            Instant.now().plus(properties.getAccessTokenExpirationMinutes(), ChronoUnit.MINUTES));

        String token = Jwts.builder()
            .signWith(SignatureAlgorithm.HS512, properties.getSecretKey())
            .setIssuedAt(new Date())
            .setSubject(member.getId().toString())
            .setExpiration(expires)
            .claim(ROLE_CLAIM, member.getMemberRole().name())
            .compact();

        return TokenDto.builder()
            .token(token)
            .expires(expires)
            .build();
    }

    /**
     * 리프레시 토큰을 생성하여 반환합니다.
     * @return 토큰 정보
     */
    public TokenDto createRefreshToken(Member member) {
        Date expires = Date.from(
            Instant.now().plus(properties.getRefreshTokenExpirationHours(), ChronoUnit.HOURS));

        String token = Jwts.builder()
            .signWith(SignatureAlgorithm.HS512, properties.getSecretKey())
            .setIssuedAt(new Date())
            .setSubject(member.getId().toString())
            .setExpiration(expires)
            .claim(ROLE_CLAIM, member.getMemberRole().name())
            .compact();

        return TokenDto.builder()
            .token(token)
            .expires(expires)
            .build();
    }

    /**
     * 토큰이 유효하면 토큰 데이터를 기반으로 인증 객체를 생성하여 반환합니다.
     * @param token JWT
     * @return 토큰이 유효하면 인증 객체, 실패 시 null
     */
    public Authentication authenticate(String token) {
        try {
            Jws<Claims> claims = Jwts.parser()
                .setSigningKey(properties.getSecretKey())
                .parseClaimsJws(token);

            Claims body = claims.getBody();
            List<GrantedAuthority> authorities = getAuthoritiesFromClaims(body);

            return new UsernamePasswordAuthenticationToken(
                Long.parseLong(body.getSubject()),
                null,
                authorities
            );
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * JWT 클레임에서 권한 정보를 추출하고, 스프링 시큐리티가 지원하는 권한 타입으로 반환합니다.
     * @param body JWT 클레임
     * @return 스프링 시큐리티 권한 타입
     * @throws IllegalArgumentException
     *          권한 정보 변환에 실패한 경우
     */
    private List<GrantedAuthority> getAuthoritiesFromClaims(Claims body) {
        String memberRole = body.get(ROLE_CLAIM, String.class);
        if (memberRole == null) {
            throw new IllegalArgumentException("권한 정보가 없습니다.");
        }

        return List.of(new SimpleGrantedAuthority("ROLE_" + memberRole));
    }
}
