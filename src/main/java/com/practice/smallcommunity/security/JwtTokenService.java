package com.practice.smallcommunity.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.domain.member.RoleType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
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
public class JwtTokenService {

    private static final String ISSUER = "S-Community";
    private static final String ROLE_CLAIM = "ROLE";

    @Value("${jwt.secret-key}")
    private String secretKey = "dummy";

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 회원 정보를 기반으로 토큰을 생성하여 반환합니다.
     * @param member 회원
     * @return JWT 토큰
     */
    public String createToken(Member member) {
        String roleClaim = getRoleClaim(member);

        return Jwts.builder()
            .signWith(SignatureAlgorithm.HS512, secretKey.getBytes(StandardCharsets.UTF_8))
            .setIssuer(ISSUER)
            .setIssuedAt(new Date())
            .setSubject(member.getUsername())
            .setExpiration(
                Date.from(
                    Instant.now()
                        .plus(1, ChronoUnit.DAYS))
            )
            .claim(ROLE_CLAIM, roleClaim)
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
                .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
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
     * 회원이 가진 권한 목록을 JSON 형식으로 반환합니다.
     * @param member 회원
     * @return JSON 문자열 형식으로 변환된 회원 권한 목록
     * @throws IllegalArgumentException
     *          권한을 JSON 형식으로 변환하지 못한 경우
     */
    private String getRoleClaim(Member member) {
        List<RoleType> roleTypes = member.getMemberRoles().stream()
            .map(memberRole -> memberRole.getRole().getRoleType())
            .collect(Collectors.toList());

        try {
            return objectMapper.writeValueAsString(roleTypes);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("회원 권한 토큰화에 실패했습니다.", e);
        }
    }

    /**
     * JWT 클레임에서 권한 정보를 추출하고, 스프링 시큐리티가 지원하는 권한 타입으로 반환합니다.
     * @param body JWT 클레임
     * @return 권한 목록
     * @throws IllegalArgumentException
     *          권한 정보 추출을 실패한 경우
     */
    private List<GrantedAuthority> getAuthoritiesFromClaims(Claims body) {
        try {
            String roleClaim = body.get(ROLE_CLAIM, String.class);

            return Arrays.stream(objectMapper.readValue(roleClaim, RoleType[].class))
                .map(roleType -> new SimpleGrantedAuthority(roleType.name()))
                .collect(Collectors.toList());
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("회원 권한 정보 추출에 실패했습니다.", e);
        }
    }
}
