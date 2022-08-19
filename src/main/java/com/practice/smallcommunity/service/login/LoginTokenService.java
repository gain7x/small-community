package com.practice.smallcommunity.service.login;

import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.repository.member.MemberRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class LoginTokenService {

    @Value("${jwt.secret-key}")
    private String secretKey = "dummy";

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public String issuance(String username, String password) {
        Member findMember = memberRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("회원 정보가 없습니다."));

        boolean matches = passwordEncoder.matches(password, findMember.getPassword());
        if (!matches) {
            throw new IllegalArgumentException("회원 정보가 다릅니다.");
        }

        return Jwts.builder()
            .signWith(SignatureAlgorithm.HS512, secretKey)
            .setIssuer("S-Community")
            .setIssuedAt(new Date())
            .setSubject(findMember.getUsername())
            .setExpiration(
                Date.from(
                    Instant.now()
                        .plus(1, ChronoUnit.DAYS))
            )
            .compact();
    }
}
