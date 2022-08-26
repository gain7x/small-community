package com.practice.smallcommunity.application.exception;

import java.util.List;

/**
 * 유효성 검증 예외입니다.
 */
public class ValidationErrorException extends RuntimeException {

    private final List<ValidationError> errors;

    public ValidationErrorException(String message, ValidationError error) {
        super(message);
        this.errors = List.of(error);
    }

    public ValidationErrorException(String message, List<ValidationError> errors, Throwable throwable) {
        super(message, throwable);
        this.errors = errors;
    }

    public List<ValidationError> getErrors() {
        return errors;
    }
}
