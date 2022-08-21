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

@RequiredArgsConstructor
public class JwtTokenService {

    private static final String ISSUER = "S-Community";
    private static final String ROLE_CLAIM = "ROLE";

    @Value("${jwt.secret-key}")
    private String secretKey = "dummy";

    private final ObjectMapper objectMapper = new ObjectMapper();

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
