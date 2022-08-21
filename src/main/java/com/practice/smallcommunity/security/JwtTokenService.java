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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

@RequiredArgsConstructor
public class JwtTokenService {

    @Value("${jwt.secret-key}")
    private String secretKey = "dummy";

    public String createToken(Member member) {

        return Jwts.builder()
            .signWith(SignatureAlgorithm.HS512, secretKey.getBytes(StandardCharsets.UTF_8))
            .setIssuer("S-Community")
            .setIssuedAt(new Date())
            .setSubject(member.getUsername())
            .setExpiration(
                Date.from(
                    Instant.now()
                        .plus(1, ChronoUnit.DAYS))
            )
            .compact();
    }

    public Authentication getAuthentication(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser()
                .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(jwtToken);
            Claims body = claims.getBody();

            return new UsernamePasswordAuthenticationToken(
                body.getSubject(),
                null,
                null
            );
        } catch (Exception e) {
            return null;
        }
    }
}
