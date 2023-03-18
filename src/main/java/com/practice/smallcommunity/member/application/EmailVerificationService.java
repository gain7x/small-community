package com.practice.smallcommunity.member.application;

import com.practice.smallcommunity.member.domain.EmailVerificationToken;
import com.practice.smallcommunity.member.domain.EmailVerificationTokenRepository;
import com.practice.smallcommunity.common.exception.BusinessException;
import com.practice.smallcommunity.common.exception.ErrorCode;
import com.practice.smallcommunity.infrastructure.TemplateMailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class EmailVerificationService {

    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final TemplateMailSender mailSender;

    @Value("${verification.email.timeoutSecond}")
    private long mailVerificationTimeout = 60;

    @Value("${verification.email.api}")
    private String verificationApi = "/members/verify";

    /**
     * 이메일 인증 키를 발급합니다.
     * @param email 인증 대상 이메일
     * @param redirectUri 인증 성공 후 리다이렉트하는 URI
     */
    public void sendVerificationMail(String email, String redirectUri) {
        EmailVerificationToken token = createToken(email);
        String verificationUri = makeVerificationUri(token, redirectUri);
        Map<String, Object> model = Map.of("action", verificationUri);

        mailSender.send(email, "작은 커뮤니티 인증 메일", "email/verify-email-template", model);
    }

    private EmailVerificationToken createToken(String email) {
        return emailVerificationTokenRepository.save(
                EmailVerificationToken.builder()
                        .email(email)
                        .key(UUID.randomUUID().toString())
                        .expirationSeconds(mailVerificationTimeout)
                        .build());
    }

    private String makeVerificationUri(EmailVerificationToken token, String redirectUri) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .scheme("https")
                .path(verificationApi)
                .queryParam("email", token.getEmail())
                .queryParam("key", token.getKey())
                .queryParam("redirectUri", redirectUri)
                .toUriString();
    }

    /**
     * 인증 정보를 확인하고, 유효한 경우 인증 토큰을 반환합니다.
     * @param email 인증 대상 이메일
     * @param key   인증 키
     * @return 인증 토큰
     * @throws BusinessException
     *          인증 정보가 유효하지 않은 경우
     */
    public EmailVerificationToken check(String email, String key) {
        Optional<EmailVerificationToken> verificationToken = emailVerificationTokenRepository.findById(email);
        if (verificationToken.isEmpty() || !verificationToken.get().getKey().equals(key)) {
            throw new BusinessException(ErrorCode.INVALID_VERIFICATION_DATA);
        }

        return verificationToken.get();
    }
}
