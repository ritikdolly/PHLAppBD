package com.plh.foodappbackend.security;

import com.plh.foodappbackend.serviceImpl.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = getJwtFromRequest(request);

        if (StringUtils.hasText(token)) {
            try {
                String username = jwtTokenProvider.getUsernameFromToken(token);
                System.out.println("JwtAuthFilter: Token found, username: " + username);
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    System.out.println("JwtAuthFilter: UserDetails loaded: " + userDetails.getUsername());

                    if (jwtTokenProvider.validateToken(token, userDetails)) {
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        System.out.println("JwtAuthFilter: Auth set in context");
                    } else {
                        System.out.println("JwtAuthFilter: Token validation failed");
                    }
                }
            } catch (Exception e) {
                System.out.println("JwtAuthFilter: Exception: " + e.getMessage());
                e.printStackTrace();
                // Token is invalid/expired or signature mismatch.
                // We simply ignore it and let the request proceed anonymously.
                // Spring Security will permit or deny based on configuration.
            }
        } else {
            System.out.println("JwtAuthFilter: No token found");
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
