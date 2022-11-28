package com.practice.smallcommunity.application.auth;

import com.practice.smallcommunity.application.EmailService;
import com.practice.smallcommunity.application.exception.BusinessException;
import com.practice.smallcommunity.application.exception.ErrorCode;
import com.practice.smallcommunity.domain.auth.EmailVerificationToken;
import com.practice.smallcommunity.domain.auth.EmailVerificationTokenRepository;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RequiredArgsConstructor
@Service
public class EmailVerificationService {

    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final EmailService emailService;

    @Value("${verification.email.timeoutSecond}")
    private long mailVerificationTimeout = 60;

    @Value("${verification.email.api}")
    private String verificationApi = "/members/verify";

    /**
     * 이메일 인증 키를 발급합니다.
     * @param email 인증 대상 이메일
     * @param redirectUri 인증 성공 후 리다이렉트하는 URI
     */
    public void sendVerificationMail(HttpServletRequest request, String email, String redirectUri) {
        EmailVerificationToken verificationToken = EmailVerificationToken.builder()
            .email(email)
            .key(UUID.randomUUID().toString())
            .expirationSeconds(mailVerificationTimeout)
            .build();

        emailVerificationTokenRepository.save(verificationToken);

        String verificationUri = ServletUriComponentsBuilder.fromContextPath(request)
            .path(verificationApi)
            .queryParam("email", verificationToken.getEmail())
            .queryParam("key", verificationToken.getKey())
            .queryParam("redirectUri", redirectUri)
            .toUriString();
        HashMap<String, Object> model = new HashMap<>();
        model.put("action", verificationUri);

        emailService.send(email, "작은 커뮤니티 인증 메일", "email/verify-email-template", model);
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
