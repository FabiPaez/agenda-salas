package com.agendasalas.config;

import com.agendasalas.dto.ErrorResponse;
import tools.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class SecurityErrorHandler {

    private final ObjectMapper objectMapper;

    public SecurityErrorHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, exception) ->
                escribirError(
                        response,
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "Se requiere autenticación o las credenciales son inválidas"
                );
    }

    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, exception) ->
                escribirError(
                        response,
                        HttpServletResponse.SC_FORBIDDEN,
                        "No tiene permisos para realizar esta operación"
                );
    }

    private void escribirError(
            HttpServletResponse response,
            int status,
            String mensaje) throws IOException {

        ErrorResponse error = new ErrorResponse(
                status,
                mensaje,
                LocalDateTime.now()
        );

        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        objectMapper.writeValue(response.getOutputStream(), error);
    }
}