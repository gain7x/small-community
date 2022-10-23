package com.practice.smallcommunity.application.exception;

import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

/**
 * 공통 오류 코드 열거형입니다.
 */
@ToString
@Getter
public enum ErrorCode {
    // 공통
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SCE000", "정의되지 않은 오류입니다."),
    RUNTIME_ERROR(HttpStatus.BAD_REQUEST, "SCE001", "적절하지 않은 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "SCE002", "인증이 필요합니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "SCE003", "권한이 없습니다."),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "SCE004", "요청 정보가 유효하지 않습니다."),
    // 인증
    NOT_MATCH_MEMBER(HttpStatus.BAD_REQUEST, "SCE051", "회원 정보가 다릅니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.BAD_REQUEST, "SCE052", "유효하지 않은 액세스 토큰입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "SCE053", "유효하지 않은 리프레시 토큰입니다."),
    INVALID_VERIFICATION_KEY(HttpStatus.BAD_REQUEST, "SCE054", "인증 키가 유효하지 않습니다."),
    UNVERIFIED_EMAIL(HttpStatus.FORBIDDEN, "SCE055", "인증되지 않은 이메일입니다."),
    // 회원
    NOT_FOUND_MEMBER(HttpStatus.BAD_REQUEST, "SCE101", "회원을 찾을 수 없습니다."),
    DUPLICATED_EMAIL(HttpStatus.BAD_REQUEST, "SCE102", "이미 사용 중인 이메일입니다."),
    DUPLICATED_NICKNAME(HttpStatus.BAD_REQUEST, "SCE103", "이미 사용 중인 별명입니다."),
    // 카테고리
    DUPLICATED_CATEGORY(HttpStatus.BAD_REQUEST, "SCE151", "이미 존재하는 카테고리입니다."),
    NOT_FOUND_CATEGORY(HttpStatus.BAD_REQUEST, "SCE152", "카테고리를 찾을 수 없습니다."),
    // 게시글
    NOT_FOUND_POST(HttpStatus.BAD_REQUEST, "SCE201", "게시글을 찾을 수 없습니다."),
    EXIST_ACCEPTED_REPLY(HttpStatus.BAD_REQUEST, "SCE202", "이미 채택한 답글이 있습니다."),
    // 답글
    NOT_FOUND_REPLY(HttpStatus.BAD_REQUEST, "SCE251", "답글을 찾을 수 없습니다."),
    // 알림
    NOT_FOUND_NOTIFICATION(HttpStatus.BAD_REQUEST, "SCE301", "알림 메시지를 찾을 수 없습니다."),
    ;

    private final HttpStatus responseStatus;
    private final String code;
    private final String defaultMessage;

    /**
     * 오류 코드 생성자입니다.
     *  code 값은 메시지소스 등과 연결될 수 있습니다.
     * @param responseStatus 해당 오류 발생 시 반환되는 HTTP 응답 코드입니다.
     * @param code 서비스가 정의하는 임의의 오류 코드입니다.
     * @param defaultMessage 기본 오류 메시지입니다.
     */
    ErrorCode(HttpStatus responseStatus, String code, String defaultMessage) {
        this.responseStatus = responseStatus;
        this.code = code;
        this.defaultMessage = defaultMessage;
    }
}
