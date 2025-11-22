package com.shutiye.inventory_system.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Custom access denied handler.
 * Handles authorized but forbidden access attempts (when user lacks required permissions).
 * Returns a proper JSON error response instead of default Spring Security response.
 */
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtAccessDeniedHandler.class);
    
    @Override
    public void handle(HttpServletRequest request,
                      HttpServletResponse response,
                      AccessDeniedException accessDeniedException) throws IOException, ServletException {
        
        logger.warn("Access denied for user: {} - {}", 
                request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "anonymous",
                accessDeniedException.getMessage());
        
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        
        Map<String, Object> data = new HashMap<>();
        data.put("status", "error");
        data.put("message", "Forbidden: Insufficient permissions");
        data.put("error", accessDeniedException.getMessage());
        data.put("path", request.getRequestURI());
        
        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(data));
    }
}

