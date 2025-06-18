package com.strayrescue.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    // @Override
    // protected void doFilterInternal(@NonNull HttpServletRequest request, 
    //                               @NonNull HttpServletResponse response, 
    //                               @NonNull FilterChain filterChain) throws ServletException, IOException {
    //     try {
    //         String jwt = parseJwt(request);
    //         if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
    //             String username = jwtUtils.getUserNameFromJwtToken(jwt);

    //             UserDetails userDetails = userDetailsService.loadUserByUsername(username);
    //             UsernamePasswordAuthenticationToken authentication = 
    //                     new UsernamePasswordAuthenticationToken(userDetails, null, 
    //                                                           userDetails.getAuthorities());
    //             authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

    //             SecurityContextHolder.getContext().setAuthentication(authentication);
    //         }
    //     } catch (Exception e) {
    //         logger.error("Cannot set user authentication: {}", e);
    //     }

    //     filterChain.doFilter(request, response);
    // }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, 
                                @NonNull HttpServletResponse response, 
                                @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);
            logger.info("=== JWT Filter Debug ===");
            logger.info("Request URI: " + request.getRequestURI());
            logger.info("Request Method: " + request.getMethod());
            logger.info("JWT Token: " + (jwt != null ? "Present" : "Null"));
            
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getUserNameFromJwtToken(jwt);
                logger.info("Username from JWT: " + username);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                logger.info("User authorities: " + userDetails.getAuthorities());
                
                UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(userDetails, null, 
                                                            userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.info("Authentication set successfully for user: " + username);
            } else {
                logger.warn("JWT token is null or invalid");
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: " + e.getMessage());
            e.printStackTrace(); // This will show the full stack trace
        }

        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }
}