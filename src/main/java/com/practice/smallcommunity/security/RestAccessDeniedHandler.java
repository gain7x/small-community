package com.practice.smallcommunity.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.smallcommunity.application.exception.ErrorCode;
import com.practice.smallcommunity.interfaces.common.dto.ErrorResponse;
import java.io.IOException;
import java.util.Locale;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

@RequiredArgsConstructor
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final MessageSource ms;
    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
        AccessDeniedException accessDeniedException) throws IOException, ServletException {
        ErrorCode errorCode = ErrorCode.ACCESS_DENIED;
        ErrorResponse errorResponse = ErrorResponse.builder()
            .code(errorCode.getCode())
            .message(getMessage(errorCode))
            .build();

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(errorCode.getResponseStatus().value());
        response.getWriter().print(objectMapper.writeValueAsString(errorResponse));
    }

    private String getMessage(ErrorCode errorCode) {
        return ms.getMessage(errorCode.getCode(), null, errorCode.getDefaultMessage(), Locale.KOREA);
    }
}
