package com.decagon.decapay.security;

import com.decagon.decapay.apiresponse.ApiDataResponse;
import com.decagon.decapay.service.auth.TokenBlacklistService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final static Logger LOGGER = LoggerFactory.getLogger(JwtRequestFilter.class);
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String token = extractBearerToken(request);
            if (StringUtils.isNotEmpty(token) && !tokenBlacklistService.isTokenBlacklisted(token) && SecurityContextHolder.getContext().getAuthentication() == null) {
                String username = jwtUtil.extractUsername(token);
                if (username != null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    if (jwtUtil.validateToken(token, userDetails)) {
                        List<GrantedAuthority> authorities = new ArrayList<>();
                        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                                new UsernamePasswordAuthenticationToken(username, null, authorities);
                        usernamePasswordAuthenticationToken
                                .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    }
                }
            }
        } catch (JwtException e) {
            //this.writeErrorResponse("Invalid token", response, HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            this.writeErrorResponse("Unknown error has occurred", response, HttpStatus.INTERNAL_SERVER_ERROR);
            LOGGER.error("Unknown error", e);
            return;
        }
        filterChain.doFilter(request, response);

    }


    private String extractBearerToken(HttpServletRequest request) {
        final String authorizationHeader = request.getHeader("Authorization");
        String token = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
        }
        return token;
    }


    private void writeErrorResponse(String errMsg, HttpServletResponse response, HttpStatus httpStatus) {
        try {
            ApiDataResponse ar = new ApiDataResponse<>(httpStatus);
            ar.setMessage(errMsg);
            response.setStatus(httpStatus.value());
            response.setContentType("application/json");
            ObjectMapper mapper = new ObjectMapper();
            PrintWriter out = response.getWriter();
            out.write(mapper.writeValueAsString(ar));
        } catch (Exception e) {
            LOGGER.error("Unknown error", e);
        }
    }
}
