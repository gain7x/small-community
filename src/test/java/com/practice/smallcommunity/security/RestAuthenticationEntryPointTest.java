package com.practice.smallcommunity.security;

import static org.assertj.core.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

@ExtendWith(MockitoExtension.class)
class RestAuthenticationEntryPointTest {

    @Mock
    MessageSource ms;

    ObjectMapper objectMapper = new ObjectMapper();

    RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @BeforeEach
    void setUp() {
        restAuthenticationEntryPoint = new RestAuthenticationEntryPoint(ms, objectMapper);
    }

    @Test
    void 비인가_예외_코드를_JSON_형식으로_응답한다() throws ServletException, IOException {
        //given
        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();

        //when
        restAuthenticationEntryPoint.commence(request, response, null);

        //then
        assertThat(response.getContentType()).isEqualTo("application/json;charset=UTF-8");
        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }
}