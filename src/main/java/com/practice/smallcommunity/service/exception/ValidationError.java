package com.practice.smallcommunity.service.exception;

/**
 * 유효성 검증이 실패한 정보를 담는 클래스입니다.
 */
public class ValidationError {

    private final String status;
    private final String field;

    private ValidationError(String status, String field) {
        this.status = status;
        this.field = field;
    }

    public String getCode() {
        return field == null ? status : field + "." + status;
    }

    public static ValidationError of(String status) {
        return new ValidationError(status, null);
    }

    public static ValidationError of(String status, String field) {
        return new ValidationError(status, field);
    }
}
