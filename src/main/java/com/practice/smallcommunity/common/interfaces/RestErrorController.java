package com.practice.smallcommunity.common.interfaces;

import com.practice.smallcommunity.common.exception.BusinessException;
import com.practice.smallcommunity.common.exception.BusinessExceptions;
import com.practice.smallcommunity.common.exception.ErrorCode;
import com.practice.smallcommunity.common.interfaces.dto.ErrorResponse;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * REST 예외 처리 컨트롤러입니다.
 *  오류 코드 기반으로 관리되며, 메시지소스에서 오류 코드와 일치하는 메시지를 가져옵니다.
 *  일치하는 메시지가 없는 경우 오류 코드에 정의된 기본 메시지를 사용합니다.
 */
@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class RestErrorController {

    private final MessageSource ms;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> exceptionHandler(Exception e) {
        ErrorCode errorCode = ErrorCode.SERVER_ERROR;

        log.error("Exception: {}, ", errorCode, e);

        return ResponseEntity.status(errorCode.getResponseStatus())
            .body(ErrorResponse.builder()
                .code(errorCode.getCode())
                .message(getMessage(errorCode))
                .build());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> runtimeExceptionHandler(RuntimeException e) {
        ErrorCode errorCode = ErrorCode.RUNTIME_ERROR;

        log.error("RuntimeException: {}, ", errorCode, e);

        return ResponseEntity.status(errorCode.getResponseStatus())
            .body(ErrorResponse.builder()
                .code(errorCode.getCode())
                .message(getMessage(errorCode))
                .build());
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> BusinessExceptionHandler(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();
        ErrorResponse response = ErrorResponse.builder()
            .code(errorCode.getCode())
            .message(getMessage(errorCode))
            .build();

        if (e.getField() != null) {
            response.addFieldError(e.getField(), getMessage(errorCode));
        }

        log.error("BusinessException: {}, ", errorCode, e);

        return ResponseEntity.status(errorCode.getResponseStatus())
            .body(response);
    }

    @ExceptionHandler(BusinessExceptions.class)
    public ResponseEntity<ErrorResponse> BusinessExceptionsHandler(BusinessExceptions e) {
        ErrorCode errorCode = e.getErrorCode();
        ErrorResponse response = ErrorResponse.builder()
            .code(errorCode.getCode())
            .message(getMessage(errorCode))
            .build();

        for (BusinessException be : e.getErrors()) {
            response.addFieldError(be.getField(), getMessage(be.getErrorCode()));
        }

        log.error("BusinessExceptions: {}, ", errorCode, e);

        return ResponseEntity.status(errorCode.getResponseStatus())
            .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> methodValidExceptionHandler(MethodArgumentNotValidException e) {
        ErrorCode errorCode = ErrorCode.VALIDATION_ERROR;
        ErrorResponse response = ErrorResponse.builder()
            .code(errorCode.getCode())
            .message(getMessage(errorCode))
            .build();

        for (FieldError fieldError : e.getFieldErrors()) {
            response.addFieldError(fieldError.getField(), getMessage(fieldError));
        }

        log.error("MethodArgumentNotValidException: {}, ", errorCode, e);

        return ResponseEntity.status(errorCode.getResponseStatus())
            .body(response);
    }

    private String getMessage(ErrorCode errorCode) {
        return ms.getMessage(errorCode.getCode(), null, errorCode.getDefaultMessage(),
            Locale.KOREA);
    }

    private String getMessage(MessageSourceResolvable messageSourceResolvable) {
        return ms.getMessage(messageSourceResolvable, Locale.KOREA);
    }
}
