package com.practice.smallcommunity.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.smallcommunity.application.exception.ErrorCode;
import com.practice.smallcommunity.interfaces.ErrorResponse;
import java.io.IOException;
import java.util.Locale;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

@RequiredArgsConstructor
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final MessageSource ms;
    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException authException) throws IOException, ServletException {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
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
