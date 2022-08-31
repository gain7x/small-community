package com.practice.smallcommunity.security;

import com.practice.smallcommunity.domain.member.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * JWT 토큰 발급 및 검증 서비스입니다.
 */
@RequiredArgsConstructor
public class JwtTokenProvider {

    private static final String ROLE_CLAIM = "AUTH";

    @Value("${jwt.secret-key}")
    private String secretKey = "dummy";

    private byte[] key;

    @PostConstruct
    public void init() {
        key = secretKey.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * 회원 정보를 기반으로 토큰을 생성하여 반환합니다.
     * @param member 회원
     * @return JWT 토큰
     */
    public String createToken(Member member) {
        return Jwts.builder()
            .signWith(SignatureAlgorithm.HS512, key)
            .setIssuedAt(new Date())
            .setSubject(member.getId().toString())
            .setExpiration(
                Date.from(
                    Instant.now().plus(1, ChronoUnit.DAYS)))
            .claim(ROLE_CLAIM, member.getMemberRole().name())
            .compact();
    }

    /**
     * JWT 토큰 검증이 성공하면 토큰 데이터를 기반으로 인증 객체를 생성하여 반환합니다.
     * @param jwtToken 발급했던 JWT 토큰
     * @return 검증 성공 시 인증 객체, 실패 시 null
     */
    public Authentication getAuthentication(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(jwtToken);

            Claims body = claims.getBody();
            List<GrantedAuthority> authorities = getAuthoritiesFromClaims(body);

            return new UsernamePasswordAuthenticationToken(
                body.getSubject(),
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
