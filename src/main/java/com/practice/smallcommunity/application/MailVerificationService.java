package com.practice.smallcommunity.application;

import com.practice.smallcommunity.application.exception.BusinessException;
import com.practice.smallcommunity.application.exception.ErrorCode;
import com.practice.smallcommunity.domain.auth.MailVerification;
import com.practice.smallcommunity.domain.auth.MailVerificationRepository;
import java.util.HashMap;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MailVerificationService {

    private final MailVerificationRepository mailVerificationRepository;
    private final MailService mailService;

    @Value("${verification.mail.timeoutSecond}")
    private long mailVerificationTimeout = 60;

    @Value("${server.baseUrl}")
    private String baseUrl;

    @Value("${verification.mail.api}")
    private String api;

    /**
     * 이메일 인증 키를 발급합니다.
     * @param email 인증 대상 이메일
     */
    public void sendVerificationMail(String email) {
        MailVerification mailVerification = MailVerification.builder()
            .key(UUID.randomUUID().toString())
            .mail(email)
            .ttl(mailVerificationTimeout)
            .build();

        mailVerificationRepository.save(mailVerification);

        String verificationUrl = baseUrl + api + "?key=" + mailVerification.getKey();
        HashMap<String, Object> model = new HashMap<>();
        model.put("action", verificationUrl);

        mailService.send(email, "작은 커뮤니티 인증 메일", "mail/verify-email-template", model);
    }

    /**
     * 인증 키를 확인하고, 유효한 경우 인증 정보를 반환합니다.
     * @param key    인증 키
     * @return 메일 인증 정보
     * @throws BusinessException
     *          인증 키가 유효하지 않은 경우
     */
    public MailVerification check(String key) {
        return mailVerificationRepository.findById(key)
            .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_VERIFICATION_KEY));
    }
}
