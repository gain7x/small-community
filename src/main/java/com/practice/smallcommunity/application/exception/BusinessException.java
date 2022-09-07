package com.practice.smallcommunity.application.exception;

import lombok.Getter;

/**
 * 비즈니스 규칙으로 인해 던져지는 예외입니다.
 *  field: 예외와 관련된 필드명으로 널( null )일 수 있습니다.
 */
@Getter
public class BusinessException extends RuntimeException{

    private final ErrorCode errorCode;
    private final String field;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
        this.field = null;
    }

    public BusinessException(ErrorCode errorCode, String field) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
        this.field = field;
    }

    public BusinessException(ErrorCode errorCode, String field, Throwable cause) {
        super(errorCode.getDefaultMessage(), cause);
        this.errorCode = errorCode;
        this.field = field;
    }
}
