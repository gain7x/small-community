package com.practice.smallcommunity.application.exception;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

/**
 * 비즈니스 예외를 모아서 던질 때 사용하는 예외클래스입니다.
 */
@Getter
public class BusinessExceptions extends RuntimeException {

    private final ErrorCode errorCode;
    private final List<BusinessException> errors;

    public BusinessExceptions(ErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
        this.errors = new ArrayList<>();
    }

    public BusinessExceptions(ErrorCode errorCode, List<BusinessException> errors) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
        this.errors = errors;
    }

    public void addError(BusinessException e) {
        errors.add(e);
    }
}
