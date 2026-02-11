package com.example.miniproj.common.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtAuthenticationEntryPoint() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        // JwtFilter에서 setAttribute로 담아둔 에러 코드가 있는지 확인
        ErrorCode errorCode = (ErrorCode) request.getAttribute("exception");

        // 없으면 기본적으로 UNAUTHORIZED_USER (로그인이 필요합니다)
        if (errorCode == null) {
            errorCode = ErrorCode.UNAUTHORIZED_USER;
        }

        log.error("Authentication Error : {}", errorCode.getMessage());

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        String jsonResponse = objectMapper.writeValueAsString(
                ErrorResponse.builder()
                        .status(errorCode.getStatus().value())
                        .error(errorCode.getStatus().name())
                        .code(errorCode.name())
                        .message(errorCode.getMessage())
                        .build()
        );

        response.getWriter().write(jsonResponse);
    }
}
