package com.universalis.blog.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException {

        log.debug("Unauthorized request – {}", authException.getMessage());

        // Standard 401 response
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // Custom response body
        Map<String, Object> body = Map.of(
                "timestamp", System.currentTimeMillis(),
                "status", 401,
                "error", "Unauthorized",
                "message", authException.getMessage(),
                "path", request.getRequestURI()
        );

        new ObjectMapper().writeValue(response.getOutputStream(), body);
    }
}