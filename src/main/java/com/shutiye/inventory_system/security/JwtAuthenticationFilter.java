package com.shutiye.inventory_system.security;

import com.shutiye.inventory_system.modules.auth.utils.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * JWT Authentication Filter.
 * Intercepts HTTP requests and validates JWT tokens.
 * Sets authentication in Spring Security context if token is valid.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    
    private final JwtTokenProvider jwtTokenProvider;
    private final com.shutiye.inventory_system.modules.auth.services.UserService userDetailsService;
    
    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider,
                                  com.shutiye.inventory_system.modules.auth.services.UserService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        
        try {
            // Extract JWT token from request
            String token = getTokenFromRequest(request);
            
            // Validate token and set authentication
            if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
                String username = jwtTokenProvider.getUsernameFromToken(token);
                
                // Load user details
                var userDetails = userDetailsService.loadUserByUsername(username);
                
                // Extract authorities from token
                String authoritiesString = jwtTokenProvider.getAuthoritiesFromToken(token);
                var authorities = Arrays.stream(authoritiesString.split(","))
                        .map(String::trim)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
                
                // Create authentication object
                UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                authorities
                        );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // Set authentication in security context
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                logger.debug("JWT token validated and authentication set for user: {}", username);
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e.getMessage());
            // Continue with filter chain - authentication will remain null
        }
        
        // Continue with filter chain
        filterChain.doFilter(request, response);
    }
    
    /**
     * Extract JWT token from Authorization header.
     * Token should be in format: "Bearer <token>"
     * 
     * @param request HTTP servlet request
     * @return JWT token string or null if not found
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}

